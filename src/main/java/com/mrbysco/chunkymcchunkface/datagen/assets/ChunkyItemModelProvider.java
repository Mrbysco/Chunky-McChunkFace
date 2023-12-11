package com.mrbysco.chunkymcchunkface.datagen.assets;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ChunkyItemModelProvider extends ItemModelProvider {
	public ChunkyItemModelProvider(PackOutput packOutput, ExistingFileHelper helper) {
		super(packOutput, ChunkyMcChunkFace.MOD_ID, helper);
	}

	@Override
	protected void registerModels() {
		ResourceLocation location = ChunkyRegistry.CHUNK_LOADER.getId();
		withExistingParent(location.getPath(), modLoc("block/hourglass"));
	}
}
