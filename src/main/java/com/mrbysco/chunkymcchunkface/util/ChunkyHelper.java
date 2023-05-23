package com.mrbysco.chunkymcchunkface.util;

import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class ChunkyHelper {
	/**
	 * Generates a list of ChunkPos in a radius around the center
	 *
	 * @param center the center ChunkPos
	 * @param range  the radius around the center
	 * @return a list of ChunkPos
	 */
	public static List<ChunkPos> generateChunkPosList(ChunkPos center, int range) {
		List<ChunkPos> chunkPosList = new ArrayList<>();
		int centerX = center.x;
		int centerZ = center.z;

		for (int x = centerX - range; x <= centerX + range; x++) {
			for (int z = centerZ - range; z <= centerZ + range; z++) {
				ChunkPos chunkPos = new ChunkPos(x, z);
				chunkPosList.add(chunkPos);
			}
		}

		return chunkPosList;
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
