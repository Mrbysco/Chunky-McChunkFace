package com.mrbysco.chunkymcchunkface.datagen;

import com.mrbysco.chunkymcchunkface.datagen.assets.ChunkyLanguageProvider;
import com.mrbysco.chunkymcchunkface.datagen.assets.ChunkyModelProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyBlockTagProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyItemTagProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyLootProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class ChunkyDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new ChunkyRecipeProvider.Runner(packOutput, lookupProvider));
		generator.addProvider(true, new ChunkyLootProvider(packOutput, lookupProvider));
		generator.addProvider(true, new ChunkyBlockTagProvider(packOutput, lookupProvider));
		generator.addProvider(true, new ChunkyItemTagProvider(packOutput, lookupProvider));

		generator.addProvider(true, new ChunkyLanguageProvider(packOutput));
		generator.addProvider(true, new ChunkyModelProvider(packOutput));

	}
}