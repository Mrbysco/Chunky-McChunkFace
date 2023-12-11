package com.mrbysco.chunkymcchunkface.client;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.client.renderer.ChunkLoaderBER;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public class KeyHandler {
	public static final KeyMapping KEY_SHOW_BOUNDS = new KeyMapping(getKey("show_bounds"), GLFW.GLFW_KEY_BACKSLASH, getKey("category"));

	private static String getKey(String name) {
		return String.join(".", "key", ChunkyMcChunkFace.MOD_ID, name);
	}

	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			return;
		}

		if (KEY_SHOW_BOUNDS.consumeClick()) {
			ChunkLoaderBER.renderChunkRadius = !ChunkLoaderBER.renderChunkRadius;
		}
	}
}
