package com.mrbysco.chunkymcchunkface.datagen.data;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ChunkyBlockTagProvider extends BlockTagsProvider {
	public ChunkyBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper fileHelper) {
		super(generator, ChunkyMcChunkFace.MOD_ID, fileHelper);
	}

	@Override
	protected void addTags() {
		this.tag(ChunkyTags.UPGRADE_BLOCKS).addTags(BlockTags.BEACON_BASE_BLOCKS);
	}
}