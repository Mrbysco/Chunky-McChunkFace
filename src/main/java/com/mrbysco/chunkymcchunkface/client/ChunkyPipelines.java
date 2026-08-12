package com.mrbysco.chunkymcchunkface.client;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import net.minecraft.client.renderer.RenderPipelines;

public class ChunkyPipelines {
	public static final RenderPipeline LINES_NO_DEPTH = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(ChunkyMcChunkFace.modLoc("pipeline/lines_no_depth"))
			.withCull(false)
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
			.build();

	public static final RenderPipeline TRANSLUCENT = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
			.withLocation(ChunkyMcChunkFace.modLoc("pipeline/translucent"))
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.build();
}
