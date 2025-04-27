package com.mrbysco.chunkymcchunkface.datagen.assets;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.Nullable;

public class ChunkyLanguageProvider extends LanguageProvider {
	public ChunkyLanguageProvider(PackOutput packOutput) {
		super(packOutput, ChunkyMcChunkFace.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addBlock(ChunkyRegistry.CHUNK_LOADER, "Chunky McChunkFace");

		add("chunkymcchunkface.command.list", "Chunk loaders in dimension <%s>");
		add("chunkymcchunkface.command.list.empty", "No known Chunk loaders located in <%s>");
		add("chunkymcchunkface.command.disable", "Disabled chunk loader at position %s %s %s");
		add("chunkymcchunkface.command.disable.error", "Something went wrong while disabling the chunk loader at position %s %s %s");
		add("chunkymcchunkface.command.disableall", "Disabled all active chunk loaders in dimension <%s>");
		add("chunkymcchunkface.command.disableall.empty", "Unable to chunk loaders as there are no known active chunk loaders in dimension <%s>");

		add("chunkymcchunkface.shift.text", "Hold SHIFT for more information");
		add("chunkymcchunkface.extend.text", "Expand the loaded area by building a pyramid of allowed blocks beneath the chunk loader");
		add("chunkymcchunkface.blocks.text", "An example of an allowed block is %s");

		add("key.chunkymcchunkface.category", "Chunky McChunkFace");
		add("key.chunkymcchunkface.show_bounds", "Show Chunk Loader Radius");

		add("config.jade.plugin_chunkymcchunkface.show_time", "Show time left");
		add("chunkymcchunkface.waila.time.online", "Player is online, timer is not active");
		add("chunkymcchunkface.waila.time.remaining", "Time left: %s second(s)");
		add("chunkymcchunkface.waila.time.disabled", "Timer has been disabled in config");

		addConfig("Range", "Range", "Range Settings");
		addConfig("baseRange", "Base Range", "The base range around the chunk loader that will be loaded (0 = 1 chunk only) [default: 0]");
		addConfig("tier1Range", "Tier 1 Range", "The range of a tier 1 chunk loader (1 = 3x3) [default: 1]");
		addConfig("tier2Range", "Tier 2 Range", "The range of a tier 2 chunk loader (2 = 5x5) [default: 2]");
		addConfig("tier3Range", "Tier 3 Range", "The range of a tier 3 chunk loader (3 = 7x7) [default: 3]");
		addConfig("tier4Range", "Tier 4 Range", "The range of a tier 4 chunk loader (4 = 9x9) [default: 4]");
		addConfig("time", "Time", "Time Settings");
		addConfig("offlineTime", "Offline Time", "The time in ticks that the chunk loader will stay loaded after all players that interacted with it have gone offline (168000 = 7 in-game days worth of time (140 minutes)) [default: 168000]");
	}

	/**
	 * Add the translation for a config entry
	 *
	 * @param path        The path of the config entry
	 * @param name        The name of the config entry
	 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
	 */
	private void addConfig(String path, String name, @Nullable String description) {
		this.add("chunkymcchunkface.configuration." + path, name);
		if (description != null && !description.isEmpty())
			this.add("chunkymcchunkface.configuration." + path + ".tooltip", description);
	}
}
