package com.mrbysco.chunkymcchunkface.client;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public abstract class ChunkyRenderTypes {

	public static final RenderType CHUNKY_LINE = RenderType.create(
			"chunkymcchunkface:lines_no_depth",
			RenderSetup.builder(ChunkyPipelines.LINES_NO_DEPTH)
					.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
					.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
					.createRenderSetup()
	);

	public static final RenderType CHUNKY_TRANSLUCENT = RenderType.create(
			"chunkymcchunkface:translucent",
			RenderSetup.builder(RenderPipelines.DEBUG_QUADS)
					.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
					.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
					.createRenderSetup()
	);
}
