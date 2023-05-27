package com.mrbysco.chunkymcchunkface.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import com.mrbysco.chunkymcchunkface.client.LineRenderType;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class ChunkLoaderBER implements BlockEntityRenderer<ChunkLoaderBlockEntity> {
	public ChunkLoaderBER(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(ChunkLoaderBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
					   MultiBufferSource bufferSource, int combinedLightIn, int combinedOverlayIn) {
		final Minecraft mc = Minecraft.getInstance();
		final LocalPlayer player = mc.player;

		if (player == null) return;

		if (player.getMainHandItem().is(ChunkyRegistry.CHUNK_LOADER_ITEM.get())) {
			final RenderType renderType = LineRenderType.lineRenderType(8.0F);
			final BlockPos loaderPos = blockEntity.getBlockPos();
			VertexConsumer builder = bufferSource.getBuffer(renderType);
			AABB box = AABB.of(
					new BoundingBox(loaderPos)
			);
			box = box.inflate(0.01F);

			poseStack.pushPose();

			poseStack.translate(-loaderPos.getX(), -loaderPos.getY(), -loaderPos.getZ());
			float[] onColor = new float[]{1F, 0.843137255F, 0, 1.0F};
			float[] offColor = new float[]{0.5F, 0F, 0.125F, 1.0F};

			float[] colorToUse = blockEntity.isEnabled() ? onColor : offColor;
			LevelRenderer.renderLineBox(poseStack, builder, box, colorToUse[0], colorToUse[1], colorToUse[2], colorToUse[3]);

			if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
				bufferSource1.endBatch(renderType);
			}
			poseStack.popPose();
		}
	}

	@Override
	public boolean shouldRenderOffScreen(ChunkLoaderBlockEntity dispatcher) {
		return true;
	}

	@Override
	public int getViewDistance() {
		return 256;
	}
}
