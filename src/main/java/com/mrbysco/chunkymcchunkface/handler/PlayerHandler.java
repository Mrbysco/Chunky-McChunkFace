package com.mrbysco.chunkymcchunkface.handler;

import com.mrbysco.chunkymcchunkface.data.ChunkData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerHandler {
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getPlayer();
		Level level = player.level;
		if (!level.isClientSide) {
			ChunkData data = ChunkData.get(level);
			data.removePlayer(player.getUUID());
			data.setDirty();
			data.reloadChunks(level.getServer());
		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = event.getPlayer();
		Level level = player.level;
		if (!level.isClientSide) {
			ChunkData data = ChunkData.get(level);
			data.addPlayer(player.getUUID(), level.getGameTime());
			data.setDirty();
			data.reloadChunks(level.getServer());
		}
	}
}
