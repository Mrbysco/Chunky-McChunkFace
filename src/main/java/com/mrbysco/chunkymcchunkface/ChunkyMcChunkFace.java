package com.mrbysco.chunkymcchunkface;

import com.mojang.logging.LogUtils;
import com.mrbysco.chunkymcchunkface.client.ClientHandler;
import com.mrbysco.chunkymcchunkface.config.ChunkyConfig;
import com.mrbysco.chunkymcchunkface.handler.PlayerHandler;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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

		ChunkyRegistry.BLOCKS.register(eventBus);
		ChunkyRegistry.BLOCK_ENTITIES.register(eventBus);
		ChunkyRegistry.ITEMS.register(eventBus);

		MinecraftForge.EVENT_BUS.register(new PlayerHandler());

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::onClientSetup);
		});
	}
}
