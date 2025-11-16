package com.mrbysco.chunkymcchunkface.handler;

import com.mrbysco.chunkymcchunkface.data.ChunkData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PlayerHandler {
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		Level level = player.level();
		if (!level.isClientSide()) {
			ChunkData data = ChunkData.get(level);
			data.removePlayer(player.getUUID());
		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = event.getEntity();
		Level level = player.level();
		if (!level.isClientSide()) {
			ChunkData data = ChunkData.get(level);
			data.addPlayer(player.getUUID(), level.getGameTime());
		}
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		Level level = player.level();
		if (!level.isClientSide() && level.getGameTime() % 20L == 0L) {
			ChunkData data = ChunkData.get(level);
			if (level.getGameTime() > data.getLastSeen(player.getUUID())) {
				data.addPlayer(player.getUUID(), level.getGameTime());
			}
		}
	}
}
