package com.mrbysco.chunkymcchunkface.blocks;

import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import com.mrbysco.chunkymcchunkface.data.ChunkData;
import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import com.mrbysco.chunkymcchunkface.registry.ChunkyTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChunkLoaderBlock extends BaseEntityBlock {
	public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

	public ChunkLoaderBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(ENABLED, Boolean.valueOf(false)));
	}

	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ChunkLoaderBlockEntity(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, ChunkyRegistry.CHUNK_LOADER_ENTITY.get(), ChunkLoaderBlockEntity::serverTick);
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			if (player instanceof FakePlayer) return InteractionResult.FAIL;

			if (level.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity blockEntity) {
				level.setBlockAndUpdate(pos, state.setValue(ENABLED, Boolean.valueOf(true)));
				blockEntity.addPlayer(player.getUUID());
				blockEntity.enableChunkLoading();
			}

			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state1, boolean p_60570_) {
		if (!level.isClientSide) {
			//Add to ChunkLoader map
			ChunkData data = ChunkData.get(level);
			data.addChunkLoaderPosition(level, pos);
			data.setDirty();
		}
		super.onPlace(state, level, pos, state1, p_60570_);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean p_51542_) {
		if (!state.is(newState.getBlock())) {
			if (!level.isClientSide) {
				if (level.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity blockEntity) {
					//Remove chunk loading
					blockEntity.disableChunkLoader();

					//Remove from ChunkLoader map
					ChunkData data = ChunkData.get(level);
					data.removeChunkLoaderPosition(level, pos);
					data.setDirty();
				}
			}

			super.onRemove(state, level, pos, newState, p_51542_);
		}
	}

	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
		blockStateBuilder.add(ENABLED);
	}

	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		boolean flag = level.hasNeighborSignal(pos);
		//Check if the block is powered
		if (flag && state.getValue(ENABLED)) {
			if (level.getBlockEntity(pos) instanceof ChunkLoaderBlockEntity blockEntity) {
				//Disable the chunk loader and clear the player cache
				level.setBlockAndUpdate(pos, state.setValue(ENABLED, Boolean.valueOf(false)));
				blockEntity.clearPlayerCache();
				blockEntity.unloadChunks();
			}
		}
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag flag) {
		super.appendHoverText(stack, blockGetter, components, flag);
		components.add(new TextComponent("Chunk Loader").withStyle(ChatFormatting.YELLOW));
		components.add(new TextComponent(" "));
		if (Screen.hasShiftDown()) {
			components.add(new TranslatableComponent("chunkymcchunkface.extend.text").withStyle(ChatFormatting.GOLD));
			//Get a random block from the ChunkyTags.UPGRADE_BLOCKS tag every 2 seconds and get the translation key
			var tags = ForgeRegistries.BLOCKS.tags();
			if (tags != null) {
				var tag = tags.getTag(ChunkyTags.UPGRADE_BLOCKS);
				if (tag.size() > 0) {
					int index = (int) (System.currentTimeMillis() / 1000 % tag.size());
					Block randomBlock = (Block) tag.stream().toArray()[index];
					Component blockName = new TranslatableComponent(randomBlock.getDescriptionId()).withStyle(ChatFormatting.WHITE);
					components.add(new TranslatableComponent("chunkymcchunkface.blocks.text", blockName).withStyle(ChatFormatting.GREEN));
				}
			}

		} else {
			components.add(new TranslatableComponent("chunkymcchunkface.shift.text").withStyle(ChatFormatting.GRAY));
		}
	}
}
