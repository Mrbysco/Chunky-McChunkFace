package com.mrbysco.chunkymcchunkface.blocks.entity;

import com.mojang.serialization.Codec;
import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.config.ChunkyConfig;
import com.mrbysco.chunkymcchunkface.data.ChunkData;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import com.mrbysco.chunkymcchunkface.registry.ChunkyTags;
import com.mrbysco.chunkymcchunkface.util.ChunkyHelper;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueInput.TypedInputList;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueOutput.TypedOutputList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkLoaderBlockEntity extends BlockEntity {
	private static final int MAX_TIERS = 4;
	private int tier;
	private final List<UUID> playerCache = new ArrayList<>();
	protected final LongSet loadedChunks = new LongOpenHashSet();
	private int cooldown = 0;
	private boolean playerOnline = false;
	private long lastSeen = 0L;

	public ChunkLoaderBlockEntity(BlockPos pos, BlockState state) {
		super(ChunkyRegistry.CHUNK_LOADER_ENTITY.get(), pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ChunkLoaderBlockEntity blockEntity) {
		if (level.getGameTime() % 80L == 0L) {
			int tier = getUpdatedTier(level, pos);
			if (tier != blockEntity.tier) {
				ChunkyMcChunkFace.LOGGER.info("ChunkLoader at {} has been updated from tier {} to tier {}", pos, blockEntity.tier, tier);
				//Unload Chunks
				blockEntity.tier = tier;
				blockEntity.refreshChunks();
			}
		}

		if (blockEntity.isEnabled() && level.getGameTime() % 20L == 0L) {
//			ChunkyMcChunkFace.LOGGER.info("Player cache {}", blockEntity.playerCache);
//			ChunkyMcChunkFace.LOGGER.info("Checking if ChunkLoader at: " + pos + " should be disabled");
			if (blockEntity.playerCache.isEmpty()) {
//					ChunkyMcChunkFace.LOGGER.info("Chunk Loader at {} was active without cached players, disabling", pos);
				blockEntity.disableChunkLoaderState();
				blockEntity.disableChunkLoader();
			} else {
				boolean isPlayerOnline = blockEntity.isPlayerOnline();
				long latestTime = blockEntity.getLastSeen();

				if (!isPlayerOnline) {
//						ChunkyMcChunkFace.LOGGER.info("Current time {}", level.getGameTime());
//						ChunkyMcChunkFace.LOGGER.info("Player interaction time {}", latestTime);
//						ChunkyMcChunkFace.LOGGER.info("Difference {}", (level.getGameTime() - latestTime));
					int configuredTicks = ChunkyConfig.COMMON.offlineTime.get();
					if (configuredTicks != 0 && latestTime > 0 && (level.getGameTime() - latestTime) > configuredTicks) {
						ChunkyMcChunkFace.LOGGER.info("ChunkLoader at {} has been disabled due to inactivity of the players {}", pos, ChunkyHelper.formatTicks(configuredTicks));
						blockEntity.disableChunkLoaderState();
						blockEntity.disableChunkLoader();
					}
				} else {
					if (level.getGameTime() % 100L == 0L) {
						blockEntity.loadChunks();
					}
				}
			}
		}

		if (blockEntity.cooldown > 0) {
			blockEntity.cooldown--;
		}
	}

	public long getLastSeen() {
		long latestTime = 0;
		for (UUID uuid : playerCache) {
			Player player = level.getPlayerByUUID(uuid);
			ChunkData data = ChunkData.get(level);
			long lastSeen = player != null ? level.getGameTime() : data.getLastSeen(uuid);
			if (lastSeen > latestTime) {
				latestTime = lastSeen;
			}
		}
		this.lastSeen = latestTime;
		return latestTime;
	}

	public long getLastSeenCache() {
		return this.lastSeen;
	}

	public boolean isPlayerOnline() {
		boolean isPlayerOnline = false;
		for (UUID uuid : playerCache) {
			if (level.getPlayerByUUID(uuid) != null) {
				isPlayerOnline = true;

			}
		}
		this.playerOnline = isPlayerOnline;

		return isPlayerOnline;
	}

	public boolean getPlayerOnlineCache() {
		return this.playerOnline;
	}

	/**
	 * Clear the player cache and unload chunks
	 */
	public void disableChunkLoader() {
		clearPlayerCache();
		unloadChunks();
	}

	/**
	 * Change the state to disabled
	 */
	public void disableChunkLoaderState() {
		level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(ChunkLoaderBlock.ENABLED, Boolean.FALSE));
	}

	/**
	 * Clear Player Cache
	 */
	public void clearPlayerCache() {
		this.playerCache.clear();
	}

	/**
	 * Loads the chunks
	 * This method gets called when a player interacts with the block
	 * There's a second cooldown to prevent spamming
	 */
	public void enableChunkLoading() {
		if (cooldown == 0) {
			loadChunks();
			cooldown = 20;
		}
	}

	/**
	 * Unload chunks
	 */
	public void unloadChunks() {
		//Unload chunks based around the tier range
		if (level != null && !level.isClientSide()) {
			ChunkyMcChunkFace.LOGGER.debug("Attempting to remove {} chunk tickets. pos: {} world: {}",
					loadedChunks.size(), worldPosition.toShortString(), level.dimension().location());
			ServerLevel serverLevel = (ServerLevel) level;
			ChunkData data = ChunkData.get(level);

			long centerChunk = new ChunkPos(worldPosition).toLong();
			int range = getRange(tier);
			LongSet chunkPosList = ChunkyHelper.generateChunkPosList(centerChunk, range);
			List<ChunkPos> loaderList = data.getActiveChunkLoaderChunks(serverLevel);

			//Remove the chunks that contain an active ChunkLoader from the list
			loaderList.forEach(pos -> {
				long longPos = pos.toLong();
				chunkPosList.remove(longPos);
				loadedChunks.remove(longPos);
			});

			for (long pos : chunkPosList) {
				loadedChunks.remove(pos);
				ChunkyHelper.releaseChunkTicket(serverLevel, worldPosition, pos);
			}

			for (long pos : loadedChunks) {
				ChunkyHelper.releaseChunkTicket(serverLevel, worldPosition, pos);
			}
			loadedChunks.clear();
			refreshClient();

			//Remove the chunk the loader is in last
			ChunkyHelper.releaseChunkTicket(serverLevel, worldPosition, centerChunk);
		}
	}

	/**
	 * Loads chunks in a radius based on the tier supplied
	 *
	 * @param tier The tier to use for the range
	 */
	public void loadChunks(int tier) {
		if (isEnabled()) {
			//Load chunks based around the tier range
			if (level != null && !level.isClientSide()) {
				ServerLevel serverLevel = (ServerLevel) level;
				long centerChunk = new ChunkPos(worldPosition).toLong();
				int range = getRange(tier);

				LongSet chunkPosList = ChunkyHelper.generateChunkPosList(centerChunk, range);
				for (long pos : chunkPosList) {
					loadedChunks.add(pos);
					ChunkyHelper.registerChunkTicket(serverLevel, worldPosition, pos);
				}
				refreshClient();
			}
		}
	}

	/**
	 * Loads the chunks based on the current tier
	 */
	public void loadChunks() {
		this.loadChunks(getTier());
	}

	/**
	 * Unloads the chunks and reloads them based on the new tier
	 */
	public void refreshChunks() {
		//Unload chunks based around the old tier range and load chunks based around the new tier range
		if (level != null && !level.isClientSide() && isEnabled()) {
			unloadChunks();
			loadChunks(getTier());
		}
	}

	/**
	 * Gets the tier based on the amount of valid blocks below the chunk loader in a pyramid shape
	 *
	 * @param level The level
	 * @param pos   The position of the chunk loader
	 * @return The tier
	 */
	private static int getUpdatedTier(Level level, BlockPos pos) {
		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();
		int i = 0;

		for (int j = 1; j <= MAX_TIERS; i = j++) {
			int k = y - j;
			if (k < level.getMinY()) {
				break;
			}

			boolean flag = true;

			for (int l = x - j; l <= x + j && flag; ++l) {
				for (int i1 = z - j; i1 <= z + j; ++i1) {
					if (!level.getBlockState(new BlockPos(l, k, i1)).is(ChunkyTags.UPGRADE_BLOCKS)) {
						flag = false;
						break;
					}
				}
			}

			if (!flag) {
				break;
			}
		}

		return i;
	}

	/**
	 * Gets the range based on the tier
	 *
	 * @param tier The tier
	 * @return The range
	 */
	public int getRange(int tier) {
		return switch (tier) {
			case 1 -> ChunkyConfig.COMMON.tier1Range.get();
			case 2 -> ChunkyConfig.COMMON.tier2Range.get();
			case 3 -> ChunkyConfig.COMMON.tier3Range.get();
			case 4 -> ChunkyConfig.COMMON.tier4Range.get();
			default -> ChunkyConfig.COMMON.baseRange.get();
		};
	}

	/**
	 * Gets the range based on the current tier
	 */
	public int getRange() {
		return getRange(getTier());
	}

	/**
	 * Gets the tier
	 *
	 * @return The tier
	 */
	public int getTier() {
		return tier;
	}

	/**
	 * Checks if the chunk loader is enabled
	 *
	 * @return True if enabled, false otherwise
	 */
	public boolean isEnabled() {
		return getBlockState().is(ChunkyRegistry.CHUNK_LOADER.get()) && getBlockState().getValue(ChunkLoaderBlock.ENABLED);
	}

	/**
	 * Add a player to the cache of players that have interacted with the chunk loader
	 *
	 * @param uuid The player's UUID
	 */
	public void addPlayer(UUID uuid) {
		if (!playerCache.contains(uuid)) {
			playerCache.add(uuid);
			refreshClient();
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.tier = input.getIntOr("Levels", 0);
		this.cooldown = input.getIntOr("Cooldown", 0);

		if (!loadedChunks.isEmpty()) {
			if (hasLevel()) {
				unloadChunks();
			} else {
				loadedChunks.clear();
			}
		}
		TypedInputList<Long> chunks = input.listOrEmpty("loadedChunks", Codec.LONG);
		if (!chunks.isEmpty()) {
			for (long chunk : chunks) {
				loadedChunks.add(chunk);
			}
		}

		TypedInputList<UUID> cache = input.listOrEmpty("playerCache", UUIDUtil.CODEC);
		cache.forEach(playerCache::add);

		this.lastSeen = input.getLongOr("lastSeen", 0L);
		this.playerOnline = input.getBooleanOr("playerOnline", false);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);

		output.putInt("Levels", this.tier);
		output.putInt("Cooldown", this.cooldown);
		TypedOutputList<Long> loadedList = output.list("loadedChunks", Codec.LONG);
		for (long chunk : loadedChunks) {
			loadedList.add(chunk);
		}
		output.putLong("lastSeen", this.lastSeen);
		output.putBoolean("playerOnline", this.playerOnline);

		TypedOutputList<UUID> playerCacheList = output.list("playerCache", UUIDUtil.CODEC);
		for (UUID uuid : playerCache) {
			playerCacheList.add(uuid);
		}
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		//Remove chunk loading
		this.disableChunkLoader();

		//Remove from ChunkLoader map
		ChunkData data = ChunkData.get(level);
		data.removeChunkLoaderPosition(level, pos);
		data.setDirty();

		super.preRemoveSideEffects(pos, state);
	}

	@Override
	public void onDataPacket(Connection net, ValueInput valueInput) {
		super.onDataPacket(net, valueInput);

		BlockState state = level.getBlockState(getBlockPos());
		level.sendBlockUpdated(getBlockPos(), state, state, 3);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(ChunkyMcChunkFace.LOGGER)) {
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag tag = new CompoundTag();
		try (ProblemReporter.ScopedCollector problemreporter$scopedcollector = new ProblemReporter.ScopedCollector(ChunkyMcChunkFace.LOGGER)) {
			HolderLookup.Provider lookupProvider = this.level != null ? this.level.registryAccess() : VanillaRegistries.createLookup();
			TagValueOutput output = TagValueOutput.createWithContext(problemreporter$scopedcollector, lookupProvider);
			this.saveAdditional(output);
			tag.merge(output.buildResult());
		}
		return tag;
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/**
	 * Update the client whenever the chunk loader is modified
	 */
	private void refreshClient() {
		setChanged();
		BlockState state = level.getBlockState(worldPosition);
		level.sendBlockUpdated(worldPosition, state, state, 2);
	}
}
