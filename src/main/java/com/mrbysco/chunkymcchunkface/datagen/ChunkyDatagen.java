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
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChunkyDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(true, new ChunkyRecipeProvider(generator));
			generator.addProvider(true, new ChunkyLootProvider(generator));
			BlockTagsProvider provider;
			generator.addProvider(true, provider = new ChunkyBlockTagProvider(generator, helper));
			generator.addProvider(true, new ChunkyItemTagProvider(generator, provider, helper));
		}
		if (event.includeClient()) {
			generator.addProvider(true, new ChunkyLanguageProvider(generator));
			generator.addProvider(true, new ChunkyStateProvider(generator, helper));
			generator.addProvider(true, new ChunkyItemModelProvider(generator, helper));
		}
	}
}