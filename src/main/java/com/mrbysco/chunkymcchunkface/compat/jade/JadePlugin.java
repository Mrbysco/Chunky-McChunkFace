package com.mrbysco.chunkymcchunkface.compat.jade;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import com.mrbysco.chunkymcchunkface.config.ChunkyConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
	private static final ResourceLocation SHOW_TIME = new ResourceLocation(ChunkyMcChunkFace.MOD_ID, "show_time");

	@Override
	public void register(IWailaCommonRegistration registration) {

	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.addConfig(SHOW_TIME, true);
		registration.registerBlockComponent(ChunkLoaderTimeProvider.INSTANCE, ChunkLoaderBlock.class);
	}

	public enum ChunkLoaderTimeProvider implements IBlockComponentProvider {

		INSTANCE;

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
			if (pluginConfig.get(SHOW_TIME) && blockAccessor.getBlockEntity() instanceof ChunkLoaderBlockEntity blockEntity && blockEntity.isEnabled()) {
				if (blockEntity.getPlayerOnlineCache()) {
					tooltip.add(Component.translatable("chunkymcchunkface.waila.time.online"));
				} else {
					final int configuredTicks = ChunkyConfig.COMMON.offlineTime.get();
					if (configuredTicks != 0) {
						final long lastSeen = blockEntity.getLastSeenCache();
						final long ticksActive = (blockEntity.getLevel().getGameTime() - lastSeen);
						//Time left in ticks until the chunk loader is unloaded
						int ticksLeft = configuredTicks - (int) ticksActive;
						int seconds = ticksLeft / 20;

						tooltip.add(Component.translatable("chunkymcchunkface.waila.time.remaining", seconds));
					} else {
						tooltip.add(Component.translatable("chunkymcchunkface.waila.time.disabled"));
					}
				}
			}
		}

		@Override
		public ResourceLocation getUid() {
			return JadePlugin.SHOW_TIME;
		}
	}
}
