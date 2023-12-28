package com.mrbysco.chunkymcchunkface.datagen.data;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import com.mrbysco.chunkymcchunkface.registry.ChunkyTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ChunkyBlockTagProvider extends BlockTagsProvider {
	public ChunkyBlockTagProvider(DataGenerator generator, @Nullable ExistingFileHelper fileHelper) {
		super(generator, ChunkyMcChunkFace.MOD_ID, fileHelper);
	}

	public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = forgeTag("relocation_not_supported");

	private static TagKey<Block> forgeTag(String name) {
		return BlockTags.create(new ResourceLocation("forge", name));
	}

	@Override
	protected void addTags() {
		this.tag(ChunkyTags.UPGRADE_BLOCKS).addTags(BlockTags.BEACON_BASE_BLOCKS);
		this.tag(RELOCATION_NOT_SUPPORTED).add(ChunkyRegistry.CHUNK_LOADER.get());
	}
}