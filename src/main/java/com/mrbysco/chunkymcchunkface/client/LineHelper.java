package com.mrbysco.chunkymcchunkface.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.AABB;

public class LineHelper {

	public static void renderLineBox(PoseStack.Pose pose, VertexConsumer consumer, AABB box, float red, float green, float blue, float alpha) {
		renderLineBox(pose, consumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, red, green, blue);
	}

	public static void renderLineBox(PoseStack.Pose pose, VertexConsumer consumer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha, float red2, float green2, float blue2) {
		float f = (float)minX;
		float f1 = (float)minY;
		float f2 = (float)minZ;
		float f3 = (float)maxX;
		float f4 = (float)maxY;
		float f5 = (float)maxZ;
		consumer.addVertex(pose, f, f1, f2).setColor(red, green2, blue2, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f3, f1, f2).setColor(red, green2, blue2, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f, f1, f2).setColor(red2, green, blue2, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, f, f4, f2).setColor(red2, green, blue2, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, f, f1, f2).setColor(red2, green2, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
		consumer.addVertex(pose, f, f1, f5).setColor(red2, green2, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
		consumer.addVertex(pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, -1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, -1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
		consumer.addVertex(pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
		consumer.addVertex(pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, -1.0F, 0.0F);
		consumer.addVertex(pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, -1.0F, 0.0F);
		consumer.addVertex(pose, f, f1, f5).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, -1.0F);
		consumer.addVertex(pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, -1.0F);
		consumer.addVertex(pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(pose, 1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f3, f1, f5).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
		consumer.addVertex(pose, f3, f4, f5).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
	}
}
