package com.mrbysco.chunkymcchunkface;

import com.mojang.logging.LogUtils;
import com.mrbysco.chunkymcchunkface.blocks.entity.ValidationHelper;
import com.mrbysco.chunkymcchunkface.client.ClientHandler;
import com.mrbysco.chunkymcchunkface.client.KeyHandler;
import com.mrbysco.chunkymcchunkface.commands.ChunkyCommands;
import com.mrbysco.chunkymcchunkface.config.ChunkyConfig;
import com.mrbysco.chunkymcchunkface.handler.PlayerHandler;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

import java.util.Map;

@Mod(ChunkyMcChunkFace.MOD_ID)
public class ChunkyMcChunkFace {
	public static final String MOD_ID = "chunkymcchunkface";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final TicketController CONTROLLER = new TicketController(new ResourceLocation(MOD_ID, "default"),
			(serverLevel, ticketHelper) -> {
				ResourceLocation dimensionLocation = serverLevel.dimension().location();

				for (Map.Entry<BlockPos, TicketSet> entry : ticketHelper.getBlockTickets().entrySet()) {
					//Only bother looking at non ticking chunks as we don't register any "fully" ticking chunks
					BlockPos pos = entry.getKey();
					LongSet forcedChunks = entry.getValue().nonTicking();
					LongSet tickingForcedChunks = entry.getValue().ticking();

					ValidationHelper.validateTickets(serverLevel, dimensionLocation, pos, ticketHelper, forcedChunks, false);
					ValidationHelper.validateTickets(serverLevel, dimensionLocation, pos, ticketHelper, tickingForcedChunks, true);
				}
			}
	);

	public ChunkyMcChunkFace() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ChunkyConfig.commonSpec, "ChunkyMcChunkFace-common.toml");
		FMLJavaModLoadingContext.get().getModEventBus().register(ChunkyConfig.class);

		eventBus.addListener(this::registerTicketController);
		eventBus.addListener(this::fillCreativeTab);

		ChunkyRegistry.BLOCKS.register(eventBus);
		ChunkyRegistry.BLOCK_ENTITIES.register(eventBus);
		ChunkyRegistry.ITEMS.register(eventBus);

		NeoForge.EVENT_BUS.register(new PlayerHandler());
		NeoForge.EVENT_BUS.register(new ChunkyCommands());

		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerKeyMappings);
			eventBus.addListener(ClientHandler::registerEntityRenders);
			NeoForge.EVENT_BUS.addListener(KeyHandler::onClientTick);
		}
	}

	private void registerTicketController(RegisterTicketControllersEvent event) {
		event.register(CONTROLLER);
	}

	private void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS)
			event.accept(ChunkyRegistry.CHUNK_LOADER.get());
	}
}
