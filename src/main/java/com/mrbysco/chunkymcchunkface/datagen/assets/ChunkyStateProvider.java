package com.mrbysco.chunkymcchunkface.datagen.assets;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ChunkyStateProvider extends BlockStateProvider {
	public ChunkyStateProvider(DataGenerator gen, ExistingFileHelper helper) {
		super(gen, ChunkyMcChunkFace.MOD_ID, helper);
	}

	@Override
	protected void registerStatesAndModels() {
		getVariantBuilder(ChunkyRegistry.CHUNK_LOADER.get())
				.partialState().with(BlockStateProperties.ENABLED, true)
				.modelForState().modelFile(
						models().getExistingFile(modLoc("block/hourglass"))
				).addModel()
				.partialState().with(BlockStateProperties.ENABLED, false)
				.modelForState().modelFile(
						models().getExistingFile(modLoc("block/hourglass_off"))
				).addModel();
	}
}
