package com.mrbysco.chunkymcchunkface.compat.jade;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import com.mrbysco.chunkymcchunkface.config.ChunkyConfig;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaClientRegistration;
import mcp.mobius.waila.api.IWailaCommonRegistration;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
	private static final ResourceLocation SHOW_TIME = new ResourceLocation(ChunkyMcChunkFace.MOD_ID, "show_time");

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.addConfig(SHOW_TIME, true);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerComponentProvider(ChunkLoaderTimeProvider.INSTANCE, TooltipPosition.BODY, ChunkLoaderBlock.class);
	}

	public enum ChunkLoaderTimeProvider implements IComponentProvider {

		INSTANCE;

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig pluginConfig) {
			if (pluginConfig.get(SHOW_TIME) && blockAccessor.getBlockEntity() instanceof ChunkLoaderBlockEntity blockEntity && blockEntity.isEnabled()) {
				if (blockEntity.getPlayerOnlineCache()) {
					tooltip.add(new TranslatableComponent("chunkymcchunkface.waila.time.online"));
				} else {
					final int configuredTicks = ChunkyConfig.COMMON.offlineTime.get();
					if (configuredTicks != 0) {
						final long lastSeen = blockEntity.getLastSeenCache();
						final long ticksActive = (blockEntity.getLevel().getGameTime() - lastSeen);
						//Time left in ticks until the chunk loader is unloaded
						int ticksLeft = configuredTicks - (int) ticksActive;
						int seconds = ticksLeft / 20;

						tooltip.add(new TranslatableComponent("chunkymcchunkface.waila.time.remaining", seconds));
					} else {
						tooltip.add(new TranslatableComponent("chunkymcchunkface.waila.time.disabled"));
					}
				}
			}
		}
	}
}
