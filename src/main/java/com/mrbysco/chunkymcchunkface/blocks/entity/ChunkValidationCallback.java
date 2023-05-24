package com.mrbysco.chunkymcchunkface.blocks.entity;

import com.mojang.datafixers.util.Pair;
import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.util.ChunkyHelper;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.util.Map;

//Copied from Mekanism <3 https://github.com/mekanism/Mekanism/blob/1.18.x/src/main/java/mekanism/common/tile/component/TileComponentChunkLoader.java#LL228-L228C87
public class ChunkValidationCallback implements ForgeChunkManager.LoadingValidationCallback {
	public static final ChunkValidationCallback INSTANCE = new ChunkValidationCallback();

	private ChunkValidationCallback() {
	}

	@Override
	public void validateTickets(ServerLevel serverLevel, ForgeChunkManager.TicketHelper ticketHelper) {
		ResourceLocation dimensionLocation = serverLevel.dimension().location();

		for (Map.Entry<BlockPos, Pair<LongSet, LongSet>> entry : ticketHelper.getBlockTickets().entrySet()) {
			//Only bother looking at non ticking chunks as we don't register any "fully" ticking chunks
			BlockPos pos = entry.getKey();
			LongSet forcedChunks = entry.getValue().getFirst();
			LongSet tickingForcedChunks = entry.getValue().getSecond();

			validateTickets(serverLevel, dimensionLocation, pos, ticketHelper, forcedChunks, false);
			validateTickets(serverLevel, dimensionLocation, pos, ticketHelper, tickingForcedChunks, true);
		}
	}

	private void validateTickets(ServerLevel serverLevel, ResourceLocation dimensionLocation, BlockPos pos,
								 ForgeChunkManager.TicketHelper ticketHelper, LongSet forcedChunks, boolean ticking) {
		int ticketCount = forcedChunks.size();
		if (ticketCount > 0) {
			//We expect this always be the case but just in case it is empty don't bother looking up the tile
			// so that we can properly validate it
			if (serverLevel.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity blockEntity) {
				if (blockEntity.isEnabled()) {
					if (!forcedChunks.equals(blockEntity.loadedChunks)) {
						//If there is a mismatch between the chunkSet and actual chunks
						// update the chunk set to trust what chunks the loader actually has registered
						ChunkyMcChunkFace.LOGGER.debug("Mismatched chunkSet for chunk loader at position: {} in {}. Correcting.", pos, dimensionLocation);
						blockEntity.loadedChunks.clear();
						blockEntity.loadedChunks.addAll(forcedChunks);
						blockEntity.setChanged();
					}
					//Next we validate that all the chunks are still properly contained and the chunks we want to load
					// didn't change (such as from the max radius of the Chunk Loader miner becoming lower)
					LongSet chunks = ChunkyHelper.generateChunkPosList(new ChunkPos(pos).toLong(), blockEntity.getTier());
					if (chunks.isEmpty()) {
						//Probably never the case, but if we have no chunks that should be loaded anymore;
						// just release them all
						ChunkyMcChunkFace.LOGGER.warn("Removing {} chunk tickets as they are no longer valid as this loader does not expect to have any tickets even "
								+ "though it is can operate. Pos: {} World: {}", ticketCount, pos, dimensionLocation);
						releaseAllTickets(blockEntity, pos, ticketHelper);
					} else {
						//Calculate the differences to properly adjust which chunks are loaded and which ones are not
						int removed = 0;
						int added = 0;
						//Remove any chunk tickets that are not valid anymore
						LongIterator chunkIt = blockEntity.loadedChunks.iterator();
						while (chunkIt.hasNext()) {
							long chunkPos = chunkIt.nextLong();
							if (!chunks.contains(chunkPos) || ticking != blockEntity.isEnabled()) {
								//If the chunk is no longer in our chunks we want loaded or restarting changed how it should tick,
								// then we mark it for removal
								ticketHelper.removeTicket(pos, chunkPos, ticking);
								// and remove it from the set we are keeping track of
								chunkIt.remove();
								removed++;
							}
						}
						//And add any that are valid now that weren't before
						// Note: We can safely call forceChunk here as nothing is iterating the list of forced chunks
						// as the loading validators get past a
						for (long chunkPos : chunks) {
							if (blockEntity.loadedChunks.add(chunkPos) || ticking != blockEntity.isEnabled()) {
								//If we didn't already have it in our chunk set and added, or we had removed it due to it fully ticking changing,
								// then we also need to force the chunk
								ForgeChunkManager.forceChunk(serverLevel, ChunkyMcChunkFace.MOD_ID, pos, (int) chunkPos, (int) (chunkPos >> 32), true, true);
								added++;
							}
						}

						//Mark the chunk loader as being initialized
						if (removed == 0 && added == 0) {
							ChunkyMcChunkFace.LOGGER.debug("Chunk tickets for position: {} in {}, successfully validated.", pos, dimensionLocation);
						} else {
							blockEntity.setChanged();
							//Note: Info level as this may be intended/expected when configs change (for example reducing max radius),
							// or if some of it needs to be recalculated such as the miner no longer having a target chunk
							ChunkyMcChunkFace.LOGGER.info("Removed {} no longer valid chunk tickets, and added {} newly valid chunk tickets. Pos: {} World: {}",
									removed, added, pos, dimensionLocation);
						}
					}
				} else {
					ChunkyMcChunkFace.LOGGER.info("Removing {} chunk tickets as they are no longer valid. Pos: {} World: {}", ticketCount,
							pos, dimensionLocation);
					releaseAllTickets(blockEntity, pos, ticketHelper);
				}
			}
		}

	}

	private void releaseAllTickets(ChunkLoaderBlockEntity blockEntity, BlockPos pos, ForgeChunkManager.TicketHelper ticketHelper) {
		//Release all tickets associated with the chunk loader
		ticketHelper.removeAllTickets(pos);
		blockEntity.loadedChunks.clear();

		//Mark the block entity as changed, so it gets saved and updates
		blockEntity.setChanged();
	}
}