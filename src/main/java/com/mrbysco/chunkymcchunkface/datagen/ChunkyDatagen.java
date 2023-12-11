package com.mrbysco.chunkymcchunkface.datagen;

import com.mrbysco.chunkymcchunkface.datagen.assets.ChunkyItemModelProvider;
import com.mrbysco.chunkymcchunkface.datagen.assets.ChunkyLanguageProvider;
import com.mrbysco.chunkymcchunkface.datagen.assets.ChunkyStateProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyBlockTagProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyItemTagProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyLootProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChunkyDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(true, new ChunkyRecipeProvider(packOutput, lookupProvider));
			generator.addProvider(true, new ChunkyLootProvider(packOutput));
			BlockTagsProvider provider;
			generator.addProvider(true, provider = new ChunkyBlockTagProvider(packOutput, lookupProvider, helper));
			generator.addProvider(true, new ChunkyItemTagProvider(packOutput, lookupProvider, provider, helper));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new ChunkyLanguageProvider(packOutput));
			generator.addProvider(true, new ChunkyStateProvider(packOutput, helper));
			generator.addProvider(true, new ChunkyItemModelProvider(packOutput, helper));
		}
	}
}