package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ChunkyRegistry.CHUNK_LOADER.get(), RenderType.translucent());
	}
}