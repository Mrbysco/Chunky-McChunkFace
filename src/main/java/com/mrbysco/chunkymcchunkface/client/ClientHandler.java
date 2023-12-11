package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.client.renderer.ChunkLoaderBER;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ChunkyRegistry.CHUNK_LOADER.get(), RenderType.cutout());
	}

	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(KeyHandler.KEY_SHOW_BOUNDS);
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ChunkyRegistry.CHUNK_LOADER_ENTITY.get(), ChunkLoaderBER::new);
	}
}