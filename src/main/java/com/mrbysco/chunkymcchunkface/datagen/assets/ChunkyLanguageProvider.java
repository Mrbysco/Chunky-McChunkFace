package com.mrbysco.chunkymcchunkface.datagen.assets;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ChunkyLanguageProvider extends LanguageProvider {
	public ChunkyLanguageProvider(DataGenerator gen) {
		super(gen, ChunkyMcChunkFace.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		addBlock(ChunkyRegistry.CHUNK_LOADER, "Chunky McChunkFace");

		add("chunkymcchunkface.shift.text", "Hold SHIFT for more information");
		add("chunkymcchunkface.extend.text", "Expand the loaded area by building a pyramid of allowed blocks beneath the chunk loader");
		add("chunkymcchunkface.blocks.text", "An example of an allowed block is %s");
	}
}
