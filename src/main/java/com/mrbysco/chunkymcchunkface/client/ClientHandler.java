package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.client.renderer.ChunkLoaderBER;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientHandler {

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.registerCategory(KeyHandler.CATEGORY);
		event.register(KeyHandler.KEY_SHOW_BOUNDS);
	}

	@SubscribeEvent
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ChunkyRegistry.CHUNK_LOADER_ENTITY.get(), ChunkLoaderBER::new);
	}

	@SubscribeEvent
	public static void onRegisterRenderTypes(final RegisterRenderBuffersEvent event) {
		event.registerRenderBuffer(ChunkyRenderTypes.CHUNKY_TRANSLUCENT);
		event.registerRenderBuffer(ChunkyRenderTypes.CHUNKY_LINE);
	}

	@SubscribeEvent
	public static void registerRenderPipeline(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ChunkyPipelines.LINES_NO_DEPTH);
		event.registerPipeline(ChunkyPipelines.TRANSLUCENT);
	}
}