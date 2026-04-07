package com.mrbysco.chunkymcchunkface.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import com.mrbysco.chunkymcchunkface.client.ChunkyRenderTypes;
import com.mrbysco.chunkymcchunkface.client.LineHelper;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import com.mrbysco.chunkymcchunkface.util.ChunkyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkLoaderBER implements BlockEntityRenderer<ChunkLoaderBlockEntity, ChunkLoaderRenderState> {
	public static boolean renderChunkRadius = false;

	public final Map<Long, Integer> rangeMap = new HashMap<>();

	public ChunkLoaderBER(BlockEntityRendererProvider.Context context) {

	}


	@Override
	public void submit(ChunkLoaderRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
		final Minecraft mc = Minecraft.getInstance();
		final LocalPlayer player = mc.player;

		if (player == null) return;

		if (player.getMainHandItem().is(ChunkyRegistry.CHUNK_LOADER_ITEM.get())) {
			renderOutline(nodeCollector, renderState, poseStack);
		}

		if (mc.level != null && renderChunkRadius) {
			final BlockPos loaderPos = renderState.blockPos;
			final long posLong = loaderPos.asLong();
			long centerChunk = ChunkPos.pack(loaderPos);
			final int range = renderState.range;

			if (!rangeMap.containsKey(posLong)) {
				rangeMap.put(posLong, range);
			}
			List<ChunkPos> list = ChunkyHelper.generateChunkPosList(centerChunk, range).longStream().mapToObj(ChunkPos::unpack).toList();

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

			AABB finalBox = box.inflate(0.01F);

			//Render the box
			poseStack.pushPose();
			poseStack.translate(-loaderPos.getX(), -loaderPos.getY(), -loaderPos.getZ());
			nodeCollector.submitCustomGeometry(poseStack, ChunkyRenderTypes.CHUNKY_TRANSLUCENT, (pose, vertexConsumer) -> {
				renderAABB(vertexConsumer, pose, finalBox);
			});
			poseStack.popPose();

		}
	}

	public void renderAABB(VertexConsumer vertexConsumer, PoseStack.Pose last, AABB box) {
		// Set the color to translucent orange
		float red = 1.0f;
		float green = 0.65f;
		float blue = 0.0f;
		float alpha = 0.25f;
		float alphaSide = 0.15f;

		Matrix4f pose = last.pose();

		//render the bottom of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.minZ).setNormal(last, 0.0F, -1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.minZ).setNormal(last, 0.0F, -1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.maxZ).setNormal(last, 0.0F, -1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.maxZ).setNormal(last, 0.0F, -1.0F, 0.0F).setColor(red, green, blue, alpha);

		//render the top of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.minZ).setNormal(last, 0.0F, 1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.maxZ).setNormal(last, 0.0F, 1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.maxZ).setNormal(last, 0.0F, 1.0F, 0.0F).setColor(red, green, blue, alpha);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.minZ).setNormal(last, 0.0F, 1.0F, 0.0F).setColor(red, green, blue, alpha);

		//render the north side of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.minZ).setNormal(last, 0.0F, 0.0F, -1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.minZ).setNormal(last, 0.0F, 0.0F, -1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.minZ).setNormal(last, 0.0F, 0.0F, -1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.minZ).setNormal(last, 0.0F, 0.0F, -1.0F).setColor(red, green, blue, alphaSide);

		//render the south side of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.maxZ).setNormal(last, 0.0F, 0.0F, 1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.maxZ).setNormal(last, 0.0F, 0.0F, 1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.maxZ).setNormal(last, 0.0F, 0.0F, 1.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.maxZ).setNormal(last, 0.0F, 0.0F, 1.0F).setColor(red, green, blue, alphaSide);

		//render the west side of the box
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.minZ).setNormal(last, -1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.minY, (float) box.maxZ).setNormal(last, -1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.maxZ).setNormal(last, -1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.minX, (float) box.maxY, (float) box.minZ).setNormal(last, -1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);

		//render the east side of the box
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.minZ).setNormal(last, 1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.minZ).setNormal(last, 1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.maxY, (float) box.maxZ).setNormal(last, 1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
		vertexConsumer.addVertex(pose, (float) box.maxX, (float) box.minY, (float) box.maxZ).setNormal(last, 1.0F, 0.0F, 0.0F).setColor(red, green, blue, alphaSide);
	}


	/**
	 * Render an outline around the chunk loader
	 * This method is only called when the player is holding the chunk loader item
	 *
	 * @param nodeCollector     The submit node collector
	 * @param loaderRenderSTate The chunk loader block entity
	 * @param poseStack         The pose stack
	 */
	private void renderOutline(SubmitNodeCollector nodeCollector, ChunkLoaderRenderState loaderRenderSTate,
	                           PoseStack poseStack) {
		final BlockPos loaderPos = loaderRenderSTate.blockPos;
		AABB box = AABB.of(
				new BoundingBox(loaderPos)
		);
		box = box.inflate(0.01F);

		poseStack.pushPose();

		poseStack.translate(-loaderPos.getX(), -loaderPos.getY(), -loaderPos.getZ());
		float[] onColor = new float[]{1F, 0.843137255F, 0, 1.0F};
		float[] offColor = new float[]{0.5F, 0F, 0.125F, 1.0F};

		float[] colorToUse = loaderRenderSTate.enabled ? onColor : offColor;

		AABB finalBox = box;
		nodeCollector.submitCustomGeometry(poseStack, ChunkyRenderTypes.CHUNKY_LINE, (pose, vertexConsumer) ->
				LineHelper.renderLineBox(pose, vertexConsumer, finalBox, colorToUse[0], colorToUse[1], colorToUse[2], colorToUse[3])
		);

		poseStack.popPose();
	}

	@Override
	public ChunkLoaderRenderState createRenderState() {
		return new ChunkLoaderRenderState();
	}

	@Override
	public void extractRenderState(ChunkLoaderBlockEntity blockEntity, ChunkLoaderRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
		renderState.enabled = blockEntity.isEnabled();
		renderState.range = blockEntity.getRange();
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public int getViewDistance() {
		return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
	}

	@Override
	public boolean shouldRender(ChunkLoaderBlockEntity blockEntity, Vec3 pos) {
		return Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(pos.multiply(1.0, 0.0, 1.0), this.getViewDistance());
	}

	@Override
	public AABB getRenderBoundingBox(ChunkLoaderBlockEntity blockEntity) {
		return AABB.INFINITE;
	}
}
