package com.mrbysco.chunkymcchunkface.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import net.minecraft.client.renderer.RenderPipelines;

public class ChunkyPipelines {
	public static final RenderPipeline LINES_NO_DEPTH = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(ChunkyMcChunkFace.modLoc("pipeline/lines_no_depth"))
			.withCull(false)
			.withDepthWrite(false)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build();

	public static final RenderPipeline TRANSLUCENT = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
			.withLocation(ChunkyMcChunkFace.modLoc("pipeline/translucent"))
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.build();
}
