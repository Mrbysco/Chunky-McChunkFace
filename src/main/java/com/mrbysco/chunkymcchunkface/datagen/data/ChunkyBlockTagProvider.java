package com.mrbysco.chunkymcchunkface.datagen.data;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import com.mrbysco.chunkymcchunkface.registry.ChunkyTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ChunkyBlockTagProvider extends BlockTagsProvider {
	public ChunkyBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, ChunkyMcChunkFace.MOD_ID, existingFileHelper);
	}

	public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = forgeTag("relocation_not_supported");

	private static TagKey<Block> forgeTag(String name) {
		return BlockTags.create(new ResourceLocation("forge", name));
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(ChunkyTags.UPGRADE_BLOCKS).addTags(BlockTags.BEACON_BASE_BLOCKS);
		this.tag(RELOCATION_NOT_SUPPORTED).add(ChunkyRegistry.CHUNK_LOADER.get());
	}
}