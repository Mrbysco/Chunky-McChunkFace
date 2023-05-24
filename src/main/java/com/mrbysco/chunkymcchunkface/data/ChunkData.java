package com.mrbysco.chunkymcchunkface.data;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
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

	public final Map<ResourceLocation, List<Long>> chunkloaderMap;
	public final Map<UUID, Long> playerTimeMap;

	public ChunkData(Map<ResourceLocation, List<Long>> dimensionLoaderMap, Map<UUID, Long> playerTimeMap) {
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
		List<Long> loaderMap = chunkloaderMap.getOrDefault(dimensionLocation, new ArrayList<>());

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
		List<Long> loaderMap = chunkloaderMap.getOrDefault(dimensionLocation, new ArrayList<>());

		loaderMap.remove(pos.asLong());

		chunkloaderMap.put(dimensionLocation, loaderMap);
		this.setDirty();
	}

	@SuppressWarnings("deprecation")
	public List<ChunkPos> getActiveChunkLoaderChunks(ServerLevel level) {
		List<ChunkPos> chunkPosList = new ArrayList<>();
		List<Long> loaderPositions = chunkloaderMap.getOrDefault(level.dimension().location(), new ArrayList<>());
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
		Map<ResourceLocation, List<Long>> loaderMap = new HashMap<>();

		for (int i = 0; i < loaderMapTag.size(); ++i) {
			CompoundTag listTag = loaderMapTag.getCompound(i);
			String dimension = listTag.getString("Dimension");
			ResourceLocation dimensionLocation = ResourceLocation.tryParse(dimension);

			List<Long> blockPositionsList = new ArrayList<>();
			ListTag blockPositions = listTag.getList("BlockPositions", ListTag.TAG_COMPOUND);
			for (int j = 0; j < blockPositions.size(); ++j) {
				CompoundTag blockPosTag = blockPositions.getCompound(j);
				blockPositionsList.add(blockPosTag.getLong("BlockPos"));
			}
			loaderMap.put(dimensionLocation, blockPositionsList);
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
		for (Map.Entry<ResourceLocation, List<Long>> entry : chunkloaderMap.entrySet()) {
			CompoundTag loaderTag = new CompoundTag();
			loaderTag.putString("Dimension", entry.getKey().toString());

			ListTag blockPositions = new ListTag();

			for (Long pos : entry.getValue()) {
				CompoundTag blockPosTag = new CompoundTag();
				blockPosTag.putLong("BlockPos", pos);
				blockPositions.add(blockPosTag);
			}
			loaderTag.put("BlockPositions", blockPositions);

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
