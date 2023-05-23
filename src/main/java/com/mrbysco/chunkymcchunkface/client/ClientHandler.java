package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.client.renderer.ChunkLoaderBER;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ChunkyRegistry.CHUNK_LOADER.get(), RenderType.translucent());
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ChunkyRegistry.CHUNK_LOADER_ENTITY.get(), ChunkLoaderBER::new);
	}
}