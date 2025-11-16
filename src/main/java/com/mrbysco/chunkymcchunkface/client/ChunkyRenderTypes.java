package com.mrbysco.chunkymcchunkface.client;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public abstract class ChunkyRenderTypes extends RenderType {
	public ChunkyRenderTypes(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
	                         Runnable setupState, Runnable clearState) {
		super(name, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
	}

	public static RenderType CHUNKY_LINE = RenderType.create("chunkymcchunkface:lines_no_depth", 256,
			ChunkyPipelines.LINES_NO_DEPTH, RenderType.CompositeState.builder()
					.setLineState(new LineStateShard(OptionalDouble.of(8.0F)))
					.setLayeringState(VIEW_OFFSET_Z_LAYERING)
					.setOutputState(ITEM_ENTITY_TARGET)
					.createCompositeState(false));

	public static RenderType CHUNKY_TRANSLUCENT = RenderType.create("chunkymcchunkface:translucent", 256,
			RenderPipelines.DEBUG_QUADS, RenderType.CompositeState.builder()
					.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
					.setOutputState(ITEM_ENTITY_TARGET)
					.createCompositeState(false));
}
