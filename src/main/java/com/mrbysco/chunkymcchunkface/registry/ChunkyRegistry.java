package com.mrbysco.chunkymcchunkface.registry;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChunkyRegistry {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ChunkyMcChunkFace.MOD_ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ChunkyMcChunkFace.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChunkyMcChunkFace.MOD_ID);

	public static final RegistryObject<Block> CHUNK_LOADER = BLOCKS.register("chunk_loader", () ->
			new ChunkLoaderBlock(Block.Properties.of(Material.DECORATION).strength(0.8F).sound(SoundType.METAL).noOcclusion()));

	public static final RegistryObject<BlockEntityType<ChunkLoaderBlockEntity>> CHUNK_LOADER_ENTITY = BLOCK_ENTITIES.register("chunk_loader", () ->
			BlockEntityType.Builder.of(ChunkLoaderBlockEntity::new, CHUNK_LOADER.get()).build(null));
	public static final RegistryObject<Item> CHUNK_LOADER_ITEM = ITEMS.register("chunk_loader", () -> new BlockItem(CHUNK_LOADER.get(),
			new Item.Properties()));

}
