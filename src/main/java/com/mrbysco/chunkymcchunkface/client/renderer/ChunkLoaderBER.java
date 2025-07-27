package com.mrbysco.chunkymcchunkface.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import com.mrbysco.chunkymcchunkface.client.ChunkyRenderTypes;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import com.mrbysco.chunkymcchunkface.util.ChunkyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkLoaderBER implements BlockEntityRenderer<ChunkLoaderBlockEntity> {
	public static boolean renderChunkRadius = false;
	public static final int MAX_RENDER_Y = 1024;

	public final Map<Long, Integer> rangeMap = new HashMap<>();

	public ChunkLoaderBER(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(ChunkLoaderBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
		final Minecraft mc = Minecraft.getInstance();
		final LocalPlayer player = mc.player;

		if (player == null) return;

		if (player.getMainHandItem().is(ChunkyRegistry.CHUNK_LOADER_ITEM.get())) {
			renderOutline(blockEntity, poseStack, bufferSource);
		}

		if (mc.level != null && renderChunkRadius) {
			final BlockPos loaderPos = blockEntity.getBlockPos();
			final long posLong = loaderPos.asLong();
			long centerChunk = new ChunkPos(loaderPos).toLong();

			if (!rangeMap.containsKey(posLong)) {
				rangeMap.put(posLong, blockEntity.getRange());
			}
			List<ChunkPos> list = ChunkyHelper.generateChunkPosList(centerChunk, blockEntity.getRange()).stream().map(ChunkPos::new).toList();

			if (list.isEmpty()) return;

			List<AABB> boxes = new ArrayList<>();
			for (ChunkPos pos : list) {
				AABB box = AABB.of(
						new BoundingBox(
								pos.getMinBlockX(), mc.level.getMinY(), pos.getMinBlockZ(),
								pos.getMaxBlockX(), mc.level.getMaxY(), pos.getMaxBlockZ()
						)
				);
				box = box.inflate(0.01F);
				boxes.add(box);
			}

			//Merge AABB boxes in boxes list
			AABB box = boxes.getFirst();
			for (AABB aabb : boxes) {
				box = box.minmax(aabb);
			}
			box = box.inflate(0.01F);
			VertexConsumer vertexConsumer = bufferSource.getBuffer(ChunkyRenderTypes.CHUNKY_TRANSLUCENT);

			//Render the box
			poseStack.pushPose();
			poseStack.translate(-loaderPos.getX(), -loaderPos.getY(), -loaderPos.getZ());

			renderAABB(vertexConsumer, poseStack, box);

			poseStack.popPose();

			if (bufferSource instanceof MultiBufferSource.BufferSource) {
				((MultiBufferSource.BufferSource) bufferSource).endBatch();
			}
		}
	}

	public void renderAABB(VertexConsumer vertexConsumer, PoseStack poseStack, AABB box) {
		// Set the color to translucent orange
		float red = 1.0f;
		float green = 0.65f;
		float blue = 0.0f;
		float alpha = 0.25f;
		float alphaSide = 0.15f;

		poseStack.pushPose();

		PoseStack.Pose matrixLast = poseStack.last();
		Matrix4f pose = matrixLast.pose();

		//render the bottom of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.minZ).setNormal(matrixLast, 0.0F, 1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.minZ).setNormal(matrixLast, 0.0F, 1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.maxZ).setNormal(matrixLast, 0.0F, 1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.maxZ).setNormal(matrixLast, 0.0F, 1.0F, 0.0F).setColor(red, green, blue, alpha);

		//render the top of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.minZ).setNormal(matrixLast, 0.0F, -1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.maxZ).setNormal(matrixLast, 0.0F, -1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.maxZ).setNormal(matrixLast, 0.0F, -1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.minZ).setNormal(matrixLast, 0.0F, -1.0F, 0.0F).setColor(red, green, blue, alpha);

		//render the north side of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.minZ).setNormal(matrixLast, 0.0F, 0.0F, 1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.minZ).setNormal(matrixLast, 0.0F, 0.0F, 1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.minZ).setNormal(matrixLast, 0.0F, 0.0F, 1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.minZ).setNormal(matrixLast, 0.0F, 0.0F, 1.0F).setColor(red, green, blue, alphaSide);

		//render the south side of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.maxZ).setNormal(matrixLast, 0.0F, 0.0F, -1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.maxZ).setNormal(matrixLast, 0.0F, 0.0F, -1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.maxZ).setNormal(matrixLast, 0.0F, 0.0F, -1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.maxZ).setNormal(matrixLast, 0.0F, 0.0F, -1.0F).setColor(red, green, blue, alphaSide);

		//render the west side of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.minZ).setNormal(matrixLast, 1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.maxZ).setNormal(matrixLast, 1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.maxZ).setNormal(matrixLast, 1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.minZ).setNormal(matrixLast, 1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);

		//render the east side of the box
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.minZ).setNormal(matrixLast, -1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.minZ).setNormal(matrixLast, -1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.maxZ).setNormal(matrixLast, -1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.maxZ).setNormal(matrixLast, -1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);

		poseStack.popPose();
	}


	/**
	 * Render an outline around the chunk loader
	 * This method is only called when the player is holding the chunk loader item
	 *
	 * @param blockEntity  The chunk loader block entity
	 * @param poseStack    The pose stack
	 * @param bufferSource The buffer source
	 */
	private void renderOutline(ChunkLoaderBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource) {
		final BlockPos loaderPos = blockEntity.getBlockPos();
		VertexConsumer builder = bufferSource.getBuffer(ChunkyRenderTypes.CHUNKY_LINE);
		AABB box = AABB.of(
				new BoundingBox(loaderPos)
		);
		box = box.inflate(0.01F);

		poseStack.pushPose();

		poseStack.translate(-loaderPos.getX(), -loaderPos.getY(), -loaderPos.getZ());
		float[] onColor = new float[]{1F, 0.843137255F, 0, 1.0F};
		float[] offColor = new float[]{0.5F, 0F, 0.125F, 1.0F};

		float[] colorToUse = blockEntity.isEnabled() ? onColor : offColor;
		ShapeRenderer.renderLineBox(poseStack, builder, box, colorToUse[0], colorToUse[1], colorToUse[2], colorToUse[3]);

		if (bufferSource instanceof MultiBufferSource.BufferSource bufferSource1) {
			bufferSource1.endBatch(ChunkyRenderTypes.CHUNKY_LINE);
		}
		poseStack.popPose();
	}

	@Override
	public boolean shouldRender(ChunkLoaderBlockEntity blockEntity, Vec3 pos) {
		return Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(pos.multiply(1.0D, 0.0D, 1.0D), (double) this.getViewDistance());
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(ChunkLoaderBlockEntity blockEntity) {
		BlockPos pos = blockEntity.getBlockPos();
		return new net.minecraft.world.phys.AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, MAX_RENDER_Y, pos.getZ() + 1.0);
	}

	@Override
	public int getViewDistance() {
		return 128;
	}
}
