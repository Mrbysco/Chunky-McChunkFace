package com.mrbysco.chunkymcchunkface.config;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

public class ChunkyConfig {

	public static class Common {

		public final ModConfigSpec.IntValue baseRange;
		public final ModConfigSpec.IntValue tier1Range;
		public final ModConfigSpec.IntValue tier2Range;
		public final ModConfigSpec.IntValue tier3Range;
		public final ModConfigSpec.IntValue tier4Range;
		public final ModConfigSpec.IntValue offlineTime;


		Common(ModConfigSpec.Builder builder) {
			builder.comment("Range settings")
					.push("Range");

			baseRange = builder
					.comment("The base range around the chunk loader that will be loaded (0 = 1 chunk only) [default: 0]")
					.defineInRange("baseRange", 0, 0, 16);

			tier1Range = builder
					.comment("The range of a tier 1 chunk loader (1 = 3x3) [default: 1]")
					.defineInRange("tier1Range", 1, 0, 16);

			tier2Range = builder
					.comment("The range of a tier 2 chunk loader (2 = 5x5) [default: 2]")
					.defineInRange("tier2Range", 2, 0, 16);

			tier3Range = builder
					.comment("The range of a tier 3 chunk loader (3 = 7x7) [default: 3]")
					.defineInRange("tier3Range", 3, 0, 16);

			tier4Range = builder
					.comment("The range of a tier 4 chunk loader (4 = 9x9) [default: 4]")
					.defineInRange("tier4Range", 4, 0, 16);

			builder.pop();
			builder.comment("Time settings")
					.push("time");

			offlineTime = builder
					.comment("The time in ticks that the chunk loader will stay loaded after all players that interacted with it have gone offline",
							"(168000 = 7 in-game days worth of time (140 minutes)) [default: 168000]")
					.defineInRange("offlineTime", 168000, 0, Integer.MAX_VALUE);

			builder.pop();
		}
	}

	public static final ModConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		ChunkyMcChunkFace.LOGGER.debug("Loaded ChunkyMcChunkFace's config file {}", configEvent.getConfig().getFileName());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		ChunkyMcChunkFace.LOGGER.debug("ChunkyMcChunkFace's config just got changed on the file system!");
	}
}