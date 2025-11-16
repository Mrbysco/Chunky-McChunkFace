package com.mrbysco.chunkymcchunkface.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
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
			ChunkyPipelines.TRANSLUCENT, RenderType.CompositeState.builder()
					.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
					.setOutputState(ChunkyRenderTypes.TRANSLUCENT_TARGET)
					.createCompositeState(false));

	public static final RenderStateShard.OutputStateShard TRANSLUCENT_TARGET = new RenderStateShard.OutputStateShard("translucent_target", () -> {
		RenderTarget rendertarget = Minecraft.getInstance().levelRenderer.getTranslucentTarget();
		return rendertarget != null ? rendertarget : Minecraft.getInstance().getMainRenderTarget();
	});
}
