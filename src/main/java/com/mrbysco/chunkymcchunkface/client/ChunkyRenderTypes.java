package com.mrbysco.chunkymcchunkface.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class ChunkyRenderTypes extends RenderType {
	public ChunkyRenderTypes(String nameIn, VertexFormat formatIn, Mode drawMode, int bufferSizeIn, boolean useDelegateIn, boolean needsSortingIn, Runnable setupTaskIn, Runnable clearTaskIn) {
		super(nameIn, formatIn, drawMode, bufferSizeIn, useDelegateIn, needsSortingIn, setupTaskIn, clearTaskIn);
	}

	public static RenderType lineRenderType(float lineWidth) {
		return create("chunkymcchunkface:lines_no_depth",
				DefaultVertexFormat.POSITION_COLOR, Mode.LINES, 256, false, false,
				CompositeState.builder()
						.setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
						.setLineState(new LineStateShard(OptionalDouble.of(lineWidth)))
						.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
						.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
						.setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
						.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
						.setCullState(RenderStateShard.NO_CULL)
						.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
						.createCompositeState(false));
	}

	public static RenderType translucentRenderType() {
		return create("chunkymcchunkface:translucent",
				DefaultVertexFormat.POSITION_COLOR, Mode.QUADS, 256, false, true,
				CompositeState.builder()
						.setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
						.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
						.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
						.setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
						.setWriteMaskState(RenderStateShard.COLOR_WRITE)
						.setCullState(RenderStateShard.NO_CULL)
						.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
						.createCompositeState(false));
	}
}
