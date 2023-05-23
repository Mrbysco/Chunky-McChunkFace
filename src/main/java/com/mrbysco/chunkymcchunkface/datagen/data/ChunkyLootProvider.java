package com.mrbysco.chunkymcchunkface.datagen.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChunkyLootProvider extends LootTableProvider {
	public ChunkyLootProvider(DataGenerator gen) {
		super(gen);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
		return ImmutableList.of(
				Pair.of(ChunkyBlockLoot::new, LootContextParamSets.BLOCK)
		);
	}

	public static class ChunkyBlockLoot extends BlockLoot {
		@Override
		protected void addTables() {
			dropSelf(ChunkyRegistry.CHUNK_LOADER.get());
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return (Iterable<Block>) ChunkyRegistry.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
		}
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationContext) {
		map.forEach((name, table) -> LootTables.validate(validationContext, name, table));
	}
}
