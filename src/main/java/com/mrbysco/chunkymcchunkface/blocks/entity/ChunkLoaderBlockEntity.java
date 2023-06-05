package com.mrbysco.chunkymcchunkface.blocks.entity;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
				boolean isPlayerOnline = false;
				long latestTime = 0;
				for (UUID uuid : blockEntity.playerCache) {
					if (level.getPlayerByUUID(uuid) != null) {
						isPlayerOnline = true;
					}

					Player player = level.getPlayerByUUID(uuid);
					ChunkData data = ChunkData.get(level);
					long lastSeen = player != null ? level.getGameTime() : data.getLastSeen(uuid);
					if (lastSeen > latestTime) {
						latestTime = lastSeen;
					}
				}

				if (!isPlayerOnline) {
//						ChunkyMcChunkFace.LOGGER.info("Current time {}", level.getGameTime());
//						ChunkyMcChunkFace.LOGGER.info("Player interaction time {}", latestTime);
//						ChunkyMcChunkFace.LOGGER.info("Difference {}", (level.getGameTime() - latestTime));
					int configuredTicks = ChunkyConfig.COMMON.offlineTime.get();
					if (latestTime > 0 && (level.getGameTime() - latestTime) > configuredTicks) {
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
		level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(ChunkLoaderBlock.ENABLED, Boolean.valueOf(false)));
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
		if (level != null && !level.isClientSide) {
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
			if (level != null && !level.isClientSide) {
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
		if (level != null && !level.isClientSide && isEnabled()) {
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
			if (k < level.getMinBuildHeight()) {
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
			default -> ChunkyConfig.COMMON.baseRange.get();
			case 1 -> ChunkyConfig.COMMON.tier1Range.get();
			case 2 -> ChunkyConfig.COMMON.tier2Range.get();
			case 3 -> ChunkyConfig.COMMON.tier3Range.get();
			case 4 -> ChunkyConfig.COMMON.tier4Range.get();
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

	public void load(CompoundTag tag) {
		super.load(tag);
		this.tier = tag.getInt("Levels");
		this.cooldown = tag.getInt("Cooldown");

		if (!loadedChunks.isEmpty()) {
			if (hasLevel()) {
				unloadChunks();
			} else {
				loadedChunks.clear();
			}
		}
		for (long chunk : tag.getLongArray("loadedChunks")) {
			loadedChunks.add(chunk);
		}

		ListTag playerCacheTag = tag.getList("playerCache", ListTag.TAG_COMPOUND);
		for (int j = 0; j < playerCacheTag.size(); ++j) {
			CompoundTag cacheTag = playerCacheTag.getCompound(j);
			playerCache.add(cacheTag.getUUID("UUID"));
		}
	}

	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("Levels", this.tier);
		tag.putInt("Cooldown", this.cooldown);
		tag.putLongArray("loadedChunks", loadedChunks.toLongArray());

		ListTag playerCacheTag = new ListTag();
		for (UUID uuid : playerCache) {
			CompoundTag cacheTag = new CompoundTag();
			cacheTag.putUUID("UUID", uuid);
			playerCacheTag.add(cacheTag);
		}
		tag.put("playerCache", playerCacheTag);

	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		load(pkt.getTag());

		BlockState state = level.getBlockState(getBlockPos());
		level.sendBlockUpdated(getBlockPos(), state, state, 3);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt);
		return nbt;
	}

	@Override
	public CompoundTag getPersistentData() {
		CompoundTag nbt = new CompoundTag();
		this.saveAdditional(nbt);
		return nbt;
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


	@Override
	public AABB getRenderBoundingBox() {
		// our render bounding box should always be the full block in case we're covered
		return INFINITE_EXTENT_AABB;
	}
}
