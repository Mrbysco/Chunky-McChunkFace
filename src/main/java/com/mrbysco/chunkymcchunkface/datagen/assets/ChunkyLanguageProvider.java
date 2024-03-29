package com.mrbysco.chunkymcchunkface.datagen.assets;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

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
	}
}
