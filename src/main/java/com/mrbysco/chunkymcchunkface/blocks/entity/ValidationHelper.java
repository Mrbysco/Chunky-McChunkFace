package com.mrbysco.chunkymcchunkface.blocks.entity;

import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.ChunkLoaderBlock;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import com.mrbysco.chunkymcchunkface.util.ChunkyHelper;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;

public class ValidationHelper {
	public static void validateTickets(ServerLevel serverLevel, ResourceLocation dimensionLocation, BlockPos pos,
									   TicketHelper ticketHelper, LongSet forcedChunks, boolean ticking) {
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

	private static void releaseAllTickets(ChunkLoaderBlockEntity blockEntity, BlockPos pos, TicketHelper ticketHelper) {
		//Release all tickets associated with the chunk loader
		ticketHelper.removeAllTickets(pos);
		blockEntity.loadedChunks.clear();

		BlockState state = blockEntity.getBlockState();
		if (state.is(ChunkyRegistry.CHUNK_LOADER.get())) {
			blockEntity.getLevel().setBlockAndUpdate(pos, state.setValue(ChunkLoaderBlock.ENABLED, Boolean.valueOf(false)));
		}

		//Mark the block entity as changed, so it gets saved and updates
		blockEntity.setChanged();
	}
}
