package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.client.renderer.ChunkLoaderBER;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(KeyHandler.KEY_SHOW_BOUNDS);

		ItemBlockRenderTypes.setRenderLayer(ChunkyRegistry.CHUNK_LOADER.get(), RenderType.cutout());
	}

	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ChunkyRegistry.CHUNK_LOADER_ENTITY.get(), ChunkLoaderBER::new);
	}
}