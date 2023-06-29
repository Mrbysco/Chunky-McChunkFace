package com.mrbysco.chunkymcchunkface;

import com.mojang.logging.LogUtils;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkValidationCallback;
import com.mrbysco.chunkymcchunkface.client.ClientHandler;
import com.mrbysco.chunkymcchunkface.client.KeyHandler;
import com.mrbysco.chunkymcchunkface.commands.ChunkyCommands;
import com.mrbysco.chunkymcchunkface.config.ChunkyConfig;
import com.mrbysco.chunkymcchunkface.handler.PlayerHandler;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ChunkyMcChunkFace.MOD_ID)
public class ChunkyMcChunkFace {
	public static final String MOD_ID = "chunkymcchunkface";
	public static final Logger LOGGER = LogUtils.getLogger();

	public ChunkyMcChunkFace() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ChunkyConfig.commonSpec, "ChunkyMcChunkFace-common.toml");
		FMLJavaModLoadingContext.get().getModEventBus().register(ChunkyConfig.class);

		eventBus.addListener(this::setup);
		eventBus.addListener(this::fillCreativeTab);

		ChunkyRegistry.BLOCKS.register(eventBus);
		ChunkyRegistry.BLOCK_ENTITIES.register(eventBus);
		ChunkyRegistry.ITEMS.register(eventBus);

		MinecraftForge.EVENT_BUS.register(new PlayerHandler());
		MinecraftForge.EVENT_BUS.register(new ChunkyCommands());

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerKeyMappings);
			eventBus.addListener(ClientHandler::registerEntityRenders);
			MinecraftForge.EVENT_BUS.addListener(KeyHandler::onClientTick);
		});
	}

	private void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			//Add chunk loading callbacks
			ForgeChunkManager.setForcedChunkLoadingCallback(MOD_ID, ChunkValidationCallback.INSTANCE);
		});
	}

	private void fillCreativeTab(CreativeModeTabEvent.BuildContents event) {
		if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS)
			event.accept(ChunkyRegistry.CHUNK_LOADER.get());
	}
}
