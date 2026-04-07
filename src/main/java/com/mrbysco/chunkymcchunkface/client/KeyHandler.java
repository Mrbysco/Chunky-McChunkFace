package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.client.renderer.ChunkLoaderBER;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(Dist.CLIENT)
public class KeyHandler {
	public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(ChunkyMcChunkFace.modLoc("category"));
	public static final KeyMapping KEY_SHOW_BOUNDS = new KeyMapping(getKey("show_bounds"), GLFW.GLFW_KEY_BACKSLASH, CATEGORY);

	private static String getKey(String name) {
		return String.join(".", "key", ChunkyMcChunkFace.MOD_ID, name);
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Pre event) {
		if (KEY_SHOW_BOUNDS.consumeClick()) {
			ChunkLoaderBER.renderChunkRadius = !ChunkLoaderBER.renderChunkRadius;
		}
	}
}
