package com.mrbysco.chunkymcchunkface.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChunkData extends SavedData {
	private static final Identifier DATA_NAME = ChunkyMcChunkFace.modLoc("chunk_data");

	public final Map<ResourceKey<Level>, LongSet> chunkloaderMap;
	public final Map<UUID, Long> playerTimeMap;

	public static Codec<LongSet> LONG_SET = Codec.LONG_STREAM.xmap(LongOpenHashSet::toSet, LongCollection::longStream);
	public static final UnboundedMapCodec<ResourceKey<Level>, LongSet> DIMENSION_LOADER_CODEC = Codec.unboundedMap(
			ResourceKey.codec(Registries.DIMENSION), LONG_SET
	);
	public static final UnboundedMapCodec<UUID, Long> PLAYER_TIME_MAP_CODEC = Codec.unboundedMap(
			UUIDUtil.STRING_CODEC, Codec.STRING.xmap(Long::parseLong, String::valueOf)
	);
	public static final Codec<ChunkData> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					DIMENSION_LOADER_CODEC.fieldOf("ChunkLoaderMap").forGetter(data -> data.chunkloaderMap),
					PLAYER_TIME_MAP_CODEC.fieldOf("PlayerTimeMap").forGetter(data -> data.playerTimeMap)
			).apply(instance, ChunkData::new));

	public ChunkData(Map<ResourceKey<Level>, LongSet> dimensionLoaderMap, Map<UUID, Long> playerTime) {
		this.chunkloaderMap = new HashMap<>();
		this.chunkloaderMap.putAll(dimensionLoaderMap);
		this.playerTimeMap = new HashMap<>();
		this.playerTimeMap.putAll(playerTime);
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
	public void addChunkLoaderPosition(@NotNull Level level, @NotNull BlockPos pos) {
		ResourceKey<Level> dimensionLocation = level.dimension();
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
	public void removeChunkLoaderPosition(@NotNull Level level, @NotNull BlockPos pos) {
		ResourceKey<Level> dimensionLocation = level.dimension();
		LongSet loaderMap = chunkloaderMap.getOrDefault(dimensionLocation, new LongOpenHashSet());

		loaderMap.remove(pos.asLong());

		chunkloaderMap.put(dimensionLocation, loaderMap);
		this.setDirty();
	}

	public List<ChunkPos> getActiveChunkLoaderChunks(@NotNull ServerLevel level) {
		List<ChunkPos> chunkPosList = new ArrayList<>();
		LongSet loaderPositions = chunkloaderMap.getOrDefault(level.dimension(), new LongOpenHashSet());
		for (long posLong : loaderPositions) {
			final BlockPos pos = BlockPos.of(posLong);
			//Check if area is loaded and if the block is active
			if (level.isAreaLoaded(pos, 1)) {
				BlockState state = level.getBlockState(pos);
				if (state.is(ChunkyRegistry.CHUNK_LOADER.get()) && state.getValue(ChunkLoaderBlock.ENABLED))
					chunkPosList.add(ChunkPos.containing(pos));
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
	public List<BlockPos> generateList(@NotNull ResourceKey<Level> dimension) {
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
	public List<BlockPos> getActivePositions(@NotNull ServerLevel level, @NotNull List<BlockPos> positions) {
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
	public long getLastSeen(@NotNull UUID uuid) {
		return playerTimeMap.getOrDefault(uuid, 0L);
	}

	/**
	 * Set the last time the player was seen
	 *
	 * @param uuid     The UUID of the player
	 * @param gameTime The last time the player was seen
	 */
	public void addPlayer(@NotNull UUID uuid, long gameTime) {
		playerTimeMap.put(uuid, gameTime);
		this.setDirty();
	}

	/**
	 * Remove the player from the map
	 *
	 * @param uuid The UUID of the player
	 */
	public void removePlayer(@NotNull UUID uuid) {
		if (playerTimeMap != null) {
			playerTimeMap.remove(uuid);
			this.setDirty();
		} else {
			ChunkyMcChunkFace.LOGGER.warn("Attempted to remove a player from the ChunkData, but the playerTimeMap is null.");
		}
	}

	public static SavedDataType<ChunkData> type() {
		return new SavedDataType<>(DATA_NAME, ChunkData::new, CODEC, null);
	}

	public static ChunkData get(@NotNull Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client level. This is wrong.");
		}
		ServerLevel overworld = level.getServer().overworld();

		SavedDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(type());
	}
}
