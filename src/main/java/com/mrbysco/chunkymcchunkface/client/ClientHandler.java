package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.client.renderer.ChunkLoaderBER;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ChunkyRegistry.CHUNK_LOADER.get(), ChunkSectionLayer.CUTOUT);
	}

	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(KeyHandler.KEY_SHOW_BOUNDS);
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ChunkyRegistry.CHUNK_LOADER_ENTITY.get(), ChunkLoaderBER::new);
	}

	public static void onRegisterRenderTypes(final RegisterRenderBuffersEvent event) {
		event.registerRenderBuffer(ChunkyRenderTypes.CHUNKY_TRANSLUCENT);
		event.registerRenderBuffer(ChunkyRenderTypes.CHUNKY_LINE);
	}

	public static void registerRenderPipeline(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ChunkyPipelines.LINES_NO_DEPTH);
	}
}