package com.mrbysco.chunkymcchunkface.datagen.data;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ChunkyItemTagProvider extends ItemTagsProvider {
	public ChunkyItemTagProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider, ExistingFileHelper fileHelper) {
		super(dataGenerator, blockTagsProvider, ChunkyMcChunkFace.MOD_ID, fileHelper);
	}

	@Override
	protected void addTags() {

	}
}