package com.mrbysco.chunkymcchunkface.registry;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ChunkyRegistry {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ChunkyMcChunkFace.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ChunkyMcChunkFace.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ChunkyMcChunkFace.MOD_ID);

	public static final DeferredBlock<ChunkLoaderBlock> CHUNK_LOADER = BLOCKS.register("chunk_loader", () ->
			new ChunkLoaderBlock(Block.Properties.of().mapColor(MapColor.GOLD).strength(0.8F).sound(SoundType.METAL).noOcclusion().pushReaction(PushReaction.BLOCK)));

	public static final Supplier<BlockEntityType<ChunkLoaderBlockEntity>> CHUNK_LOADER_ENTITY = BLOCK_ENTITIES.register("chunk_loader", () ->
			BlockEntityType.Builder.of(ChunkLoaderBlockEntity::new, CHUNK_LOADER.get()).build(null));
	public static final DeferredItem<BlockItem> CHUNK_LOADER_ITEM = ITEMS.registerSimpleBlockItem(CHUNK_LOADER);

}
