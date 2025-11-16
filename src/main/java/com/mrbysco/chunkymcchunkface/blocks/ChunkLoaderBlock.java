package com.mrbysco.chunkymcchunkface.blocks;

import com.mojang.serialization.MapCodec;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import com.mrbysco.chunkymcchunkface.data.ChunkData;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

public class ChunkLoaderBlock extends BaseEntityBlock {
	public static final MapCodec<ChunkLoaderBlock> CODEC = simpleCodec(ChunkLoaderBlock::new);
	public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

	@Override
	public MapCodec<ChunkLoaderBlock> codec() {
		return CODEC;
	}

	public ChunkLoaderBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, Boolean.FALSE));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ChunkLoaderBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide() ? null : createTickerHelper(blockEntityType, ChunkyRegistry.CHUNK_LOADER_ENTITY.get(), ChunkLoaderBlockEntity::serverTick);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult pHitResult) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			if (player instanceof FakePlayer) return InteractionResult.FAIL;

			if (level.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity blockEntity) {
				level.setBlockAndUpdate(pos, state.setValue(ENABLED, Boolean.TRUE));
				blockEntity.addPlayer(player.getUUID());
				blockEntity.enableChunkLoading();
			}

			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean p_60570_) {
		if (!level.isClientSide()) {
			//Add to ChunkLoader map
			ChunkData data = ChunkData.get(level);
			data.addChunkLoaderPosition(level, pos);
			data.setDirty();
		}
		super.onPlace(state, level, pos, state1, p_60570_);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.playerDestroy(level, player, pos, state, blockEntity, tool);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
		blockStateBuilder.add(ENABLED);
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
		boolean flag = level.hasNeighborSignal(pos);
		//Check if the block is powered
		if (flag && state.getValue(ENABLED)) {
			if (level.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity blockEntity) {
				//Disable the chunk loader and clear the player cache
				level.setBlockAndUpdate(pos, state.setValue(ENABLED, Boolean.FALSE));
				blockEntity.clearPlayerCache();
				blockEntity.unloadChunks();
			}
		}
	}
}
