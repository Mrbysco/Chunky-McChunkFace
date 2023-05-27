package com.mrbysco.chunkymcchunkface.data;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChunkData extends SavedData {
	private static final String DATA_NAME = ChunkyMcChunkFace.MOD_ID + "_data";

	public final Map<ResourceLocation, LongSet> chunkloaderMap;
	public final Map<UUID, Long> playerTimeMap;

	public ChunkData(Map<ResourceLocation, LongSet> dimensionLoaderMap, Map<UUID, Long> playerTimeMap) {
		this.chunkloaderMap = dimensionLoaderMap;
		this.playerTimeMap = playerTimeMap;
	}

	public ChunkData() {
		this(new HashMap<>(), new HashMap<>());
	}

	/**
	 * Add the chunk loader to the map
	 *
	 * @param level The level the ChunkLoader is in
	 * @param pos   The position of the ChunkLoader
	 */
	public void addChunkLoaderPosition(Level level, BlockPos pos) {
		ResourceLocation dimensionLocation = level.dimension().location();
		LongSet loaderMap = chunkloaderMap.getOrDefault(dimensionLocation, new LongOpenHashSet());

		loaderMap.add(pos.asLong());

		chunkloaderMap.put(dimensionLocation, loaderMap);
		this.setDirty();
	}

	/**
	 * Remove the chunk loader from the map
	 *
	 * @param level The level the ChunkLoader was in
	 * @param pos   The position of the ChunkLoader
	 */
	public void removeChunkLoaderPosition(Level level, BlockPos pos) {
		ResourceLocation dimensionLocation = level.dimension().location();
		LongSet loaderMap = chunkloaderMap.getOrDefault(dimensionLocation, new LongOpenHashSet());

		loaderMap.remove(pos.asLong());

		chunkloaderMap.put(dimensionLocation, loaderMap);
		this.setDirty();
	}

	@SuppressWarnings("deprecation")
	public List<ChunkPos> getActiveChunkLoaderChunks(ServerLevel level) {
		List<ChunkPos> chunkPosList = new ArrayList<>();
		LongSet loaderPositions = chunkloaderMap.getOrDefault(level.dimension().location(), new LongOpenHashSet());
		for (long posLong : loaderPositions) {
			final BlockPos pos = BlockPos.of(posLong);
			//Check if area is loaded and if the block is active
			if (level.isAreaLoaded(pos, 1)) {
				BlockState state = level.getBlockState(pos);
				if (state.is(ChunkyRegistry.CHUNK_LOADER.get()) && state.getValue(ChunkLoaderBlock.ENABLED))
					chunkPosList.add(new ChunkPos(pos));
			}
		}
		return chunkPosList;
	}

	/**
	 * Get the list of chunk loaders in the dimension
	 *
	 * @param dimension The dimension to get the chunk loaders from
	 * @return The list of chunk loaders in the dimension
	 */
	public List<BlockPos> generateList(ResourceLocation dimension) {
		List<BlockPos> positions = new ArrayList<>();
		//Get all the chunk loaders in the dimension
		LongSet chunkLoaderList = chunkloaderMap.getOrDefault(dimension, new LongOpenHashSet());
		if (!chunkLoaderList.isEmpty()) {
			chunkLoaderList.forEach(posLong -> positions.add(BlockPos.of(posLong)));
		}
		return positions;
	}

	/**
	 * Get the list of active chunk loaders in the dimension
	 *
	 * @param level     The level to get the chunk loaders from
	 * @param positions The list of positions to check
	 * @return The list of active chunk loaders in the dimension
	 */
	public List<BlockPos> getActivePositions(ServerLevel level, List<BlockPos> positions) {
		List<BlockPos> posList = new ArrayList<>(positions);
		posList.removeIf(pos -> {
			if (level.isAreaLoaded(pos, 1)) {
				BlockState state = level.getBlockState(pos);
				return !state.is(ChunkyRegistry.CHUNK_LOADER.get()) || !state.getValue(ChunkLoaderBlock.ENABLED);
			}
			return true;
		});
		return posList;
	}

	/**
	 * Get the last time the player was seen
	 *
	 * @param uuid The UUID of the player
	 * @return The last time the player was seen
	 */
	public long getLastSeen(UUID uuid) {
		return playerTimeMap.getOrDefault(uuid, 0L);
	}

	/**
	 * Set the last time the player was seen
	 *
	 * @param uuid     The UUID of the player
	 * @param gameTime The last time the player was seen
	 */
	public void addPlayer(UUID uuid, long gameTime) {
		playerTimeMap.put(uuid, gameTime);
		this.setDirty();
	}

	/**
	 * Remove the player from the map
	 *
	 * @param uuid The UUID of the player
	 */
	public void removePlayer(UUID uuid) {
		playerTimeMap.remove(uuid);
		this.setDirty();
	}

	public static ChunkData load(CompoundTag tag) {
		ListTag loaderMapTag = tag.getList("ChunkLoaderMap", CompoundTag.TAG_COMPOUND);
		Map<ResourceLocation, LongSet> loaderMap = new HashMap<>();

		for (int i = 0; i < loaderMapTag.size(); ++i) {
			CompoundTag listTag = loaderMapTag.getCompound(i);
			String dimension = listTag.getString("Dimension");
			ResourceLocation dimensionLocation = ResourceLocation.tryParse(dimension);

			LongSet chunkLoaderSet = new LongOpenHashSet();
			for (long chunk : listTag.getLongArray("BlockPositions")) {
				chunkLoaderSet.add(chunk);
			}
			loaderMap.put(dimensionLocation, chunkLoaderSet);
		}

		ListTag playerTimeTag = tag.getList("PlayerTimeMap", CompoundTag.TAG_COMPOUND);
		Map<UUID, Long> playerTimeMap = new HashMap<>();
		for (int i = 0; i < playerTimeTag.size(); ++i) {
			CompoundTag listTag = playerTimeTag.getCompound(i);
			UUID uuid = listTag.getUUID("UUID");
			long time = listTag.getLong("Time");

			playerTimeMap.put(uuid, time);
		}

		return new ChunkData(loaderMap, playerTimeMap);
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag loaderMapTag = new ListTag();
		for (Map.Entry<ResourceLocation, LongSet> entry : chunkloaderMap.entrySet()) {
			CompoundTag loaderTag = new CompoundTag();
			loaderTag.putString("Dimension", entry.getKey().toString());
			loaderTag.putLongArray("BlockPositions", entry.getValue().toLongArray());

			loaderMapTag.add(loaderTag);
		}
		tag.put("ChunkLoaderMap", loaderMapTag);

		ListTag playerTimeTag = new ListTag();
		for (Map.Entry<UUID, Long> entry : playerTimeMap.entrySet()) {
			CompoundTag playerTag = new CompoundTag();
			playerTag.putUUID("UUID", entry.getKey());
			playerTag.putLong("Time", entry.getValue());

			playerTimeTag.add(playerTag);
		}
		tag.put("PlayerTimeMap", playerTimeTag);

		return tag;
	}

	public static ChunkData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client level. This is wrong.");
		}
		ServerLevel overworld = level.getServer().overworld();

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(ChunkData::load, ChunkData::new, DATA_NAME);
	}
}
