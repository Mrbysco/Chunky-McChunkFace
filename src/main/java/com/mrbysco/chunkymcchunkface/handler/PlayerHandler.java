package com.mrbysco.chunkymcchunkface.handler;

import com.mrbysco.chunkymcchunkface.data.ChunkData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class PlayerHandler {
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		Level level = player.level();
		if (!level.isClientSide) {
			ChunkData data = ChunkData.get(level);
			data.removePlayer(player.getUUID());
		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = event.getEntity();
		Level level = player.level();
		if (!level.isClientSide) {
			ChunkData data = ChunkData.get(level);
			data.addPlayer(player.getUUID(), level.getGameTime());
		}
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		Player player = event.player;
		Level level = player.level();
		if (!level.isClientSide && level.getGameTime() % 20L == 0L) {
			ChunkData data = ChunkData.get(level);
			if (level.getGameTime() > data.getLastSeen(player.getUUID())) {
				data.addPlayer(player.getUUID(), level.getGameTime());
			}
		}
	}
}
