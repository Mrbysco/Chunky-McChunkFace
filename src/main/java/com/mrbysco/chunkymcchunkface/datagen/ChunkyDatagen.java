package com.mrbysco.chunkymcchunkface.datagen;

import com.mrbysco.chunkymcchunkface.datagen.assets.ChunkyItemModelProvider;
import com.mrbysco.chunkymcchunkface.datagen.assets.ChunkyLanguageProvider;
import com.mrbysco.chunkymcchunkface.datagen.assets.ChunkyStateProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyBlockTagProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyItemTagProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyLootProvider;
import com.mrbysco.chunkymcchunkface.datagen.data.ChunkyRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChunkyDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(new ChunkyRecipeProvider(generator));
			generator.addProvider(new ChunkyLootProvider(generator));
			BlockTagsProvider provider;
			generator.addProvider(provider = new ChunkyBlockTagProvider(generator, helper));
			generator.addProvider(new ChunkyItemTagProvider(generator, provider, helper));
		}
		if (event.includeClient()) {
			generator.addProvider(new ChunkyLanguageProvider(generator));
			generator.addProvider(new ChunkyStateProvider(generator, helper));
			generator.addProvider(new ChunkyItemModelProvider(generator, helper));
		}
	}
}