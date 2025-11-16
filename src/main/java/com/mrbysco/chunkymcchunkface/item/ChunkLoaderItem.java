package com.mrbysco.chunkymcchunkface.item;

import com.mrbysco.chunkymcchunkface.registry.ChunkyTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class ChunkLoaderItem extends BlockItem {

	public ChunkLoaderItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		tooltipAdder.accept(Component.literal("Chunk Loader").withStyle(ChatFormatting.YELLOW));
		tooltipAdder.accept(Component.literal(" "));
		if (flag.hasShiftDown()) {
			tooltipAdder.accept(Component.translatable("chunkymcchunkface.extend.text").withStyle(ChatFormatting.GOLD));
			//Get a random block from the ChunkyTags.UPGRADE_BLOCKS tag every 2 seconds and get the translation key
			var optionalTag = BuiltInRegistries.BLOCK.get(ChunkyTags.UPGRADE_BLOCKS);
			if (optionalTag.isPresent()) {
				var tag = optionalTag.get();
				if (tag.size() > 0) {
					int index = (int) (System.currentTimeMillis() / 1000 % tag.size());
					Block randomBlock = tag.stream().toList().get(index).value();
					Component blockName = Component.translatable(randomBlock.getDescriptionId()).withStyle(ChatFormatting.WHITE);
					tooltipAdder.accept(Component.translatable("chunkymcchunkface.blocks.text", blockName).withStyle(ChatFormatting.GREEN));
				}
			}

		} else {
			tooltipAdder.accept(Component.translatable("chunkymcchunkface.shift.text").withStyle(ChatFormatting.GRAY));
		}
	}
}
