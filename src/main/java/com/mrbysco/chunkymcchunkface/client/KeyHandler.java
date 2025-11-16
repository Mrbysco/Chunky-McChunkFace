package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.client.renderer.ChunkLoaderBER;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	private static KeyMapping.Category CATEGORY = new KeyMapping.Category(ChunkyMcChunkFace.modLoc("category"));
	public static final KeyMapping KEY_SHOW_BOUNDS = new KeyMapping(getKey("show_bounds"), GLFW.GLFW_KEY_BACKSLASH, CATEGORY);

	private static String getKey(String name) {
		return String.join(".", "key", ChunkyMcChunkFace.MOD_ID, name);
	}

	public static void onClientTick(ClientTickEvent.Pre event) {
		if (KEY_SHOW_BOUNDS.consumeClick()) {
			ChunkLoaderBER.renderChunkRadius = !ChunkLoaderBER.renderChunkRadius;
		}
	}
}
