package com.mrbysco.chunkymcchunkface.datagen.assets;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ChunkyModelProvider extends ModelProvider {
	public ChunkyModelProvider(PackOutput output) {
		super(output, ChunkyMcChunkFace.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		createChunkloader(blockModels, ChunkyRegistry.CHUNK_LOADER);
	}

	public void createChunkloader(BlockModelGenerators blockModels, DeferredBlock<ChunkLoaderBlock> chunkLoader) {
		ResourceLocation hourglass = modLocation("block/hourglass");
		MultiVariant onLocation = BlockModelGenerators.plainVariant(hourglass);
		MultiVariant offLocation = BlockModelGenerators.plainVariant(modLocation("block/hourglass_off"));
		blockModels.blockStateOutput
				.accept(
						MultiVariantGenerator.dispatch(chunkLoader.get())
								.with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.ENABLED, onLocation, offLocation))
				);
		blockModels.registerSimpleItemModel(chunkLoader.get(), hourglass);

	}
}
