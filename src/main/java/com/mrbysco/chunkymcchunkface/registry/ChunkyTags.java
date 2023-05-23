package com.mrbysco.chunkymcchunkface.registry;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ChunkyTags {
	public static final TagKey<Block> UPGRADE_BLOCKS = BlockTags.create(new ResourceLocation(ChunkyMcChunkFace.MOD_ID, "upgrade_blocks"));
}
