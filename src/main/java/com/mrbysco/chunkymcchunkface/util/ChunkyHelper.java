package com.mrbysco.chunkymcchunkface.util;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;

public class ChunkyHelper {

	/**
	 * Generates a set of Chunk positions in a radius around the center
	 *
	 * @param center the center ChunkPos
	 * @param range  the radius around the center
	 * @return a set of ChunkPos longs
	 */
	public static LongSet generateChunkPosList(long center, int range) {
		LongSet chunkPosList = new LongOpenHashSet();
		int centerX = (int) center;
		int centerZ = (int) (center >> 32);

		for (int x = centerX - range; x <= centerX + range; x++) {
			for (int z = centerZ - range; z <= centerZ + range; z++) {
				ChunkPos chunkPos = new ChunkPos(x, z);
				chunkPosList.add(chunkPos.toLong());
			}
		}

		return chunkPosList;
	}

	/**
	 * Registers a chunk ticket for the given chunkPos
	 *
	 * @param serverLevel the server level
	 * @param ownerPos    the position of the chunk loader
	 * @param chunkPos    the chunk position to force load
	 */
	public static void registerChunkTicket(ServerLevel serverLevel, BlockPos ownerPos, long chunkPos) {
//		ChunkyMcChunkFace.LOGGER.info("Chunk Loader at {} will now force chunk {} to stay loaded", ownerPos, chunkPos);
		ForgeChunkManager.forceChunk(serverLevel, ChunkyMcChunkFace.MOD_ID, ownerPos, (int) chunkPos, (int) (chunkPos >> 32), true, true);
	}

	/**
	 * Releases a chunk ticket for the given chunkPos
	 *
	 * @param serverLevel the server level
	 * @param ownerPos    the position of the chunk loader
	 * @param chunkPos    the chunk position to stop force loading
	 */
	public static void releaseChunkTicket(ServerLevel serverLevel, BlockPos ownerPos, long chunkPos) {
//		ChunkyMcChunkFace.LOGGER.info("Chunk Loader at {} will now force chunk {} to stay loaded", ownerPos, chunkPos);
		ForgeChunkManager.forceChunk(serverLevel, ChunkyMcChunkFace.MOD_ID, ownerPos, (int) chunkPos, (int) (chunkPos >> 32), false, true);
	}

	/**
	 * Formats ticks to a readable time format
	 *
	 * @param ticks the ticks to format
	 * @return a formatted time string
	 */
	public static String formatTicks(int ticks) {
		int totalSeconds = ticks / 20;
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}
