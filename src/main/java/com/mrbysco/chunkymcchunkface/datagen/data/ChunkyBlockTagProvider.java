package com.mrbysco.chunkymcchunkface.datagen.data;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import com.mrbysco.chunkymcchunkface.registry.ChunkyTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ChunkyBlockTagProvider extends BlockTagsProvider {
	public ChunkyBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, ChunkyMcChunkFace.MOD_ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		this.tag(ChunkyTags.UPGRADE_BLOCKS).addTags(BlockTags.BEACON_BASE_BLOCKS);
		this.tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(ChunkyRegistry.CHUNK_LOADER.getKey());
	}
}