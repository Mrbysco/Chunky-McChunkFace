package com.mrbysco.chunkymcchunkface.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.data.ChunkData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ChunkyCommands {
	@SubscribeEvent
	public void onRegisterCommandsEvent(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

		final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ChunkyMcChunkFace.MOD_ID);
		root.requires((source) -> source.hasPermission(2))
				.then(Commands.literal("list")
						.then(Commands.argument("dimension", DimensionArgument.dimension())
								.executes(this::generateList))
				);

		dispatcher.register(root);
	}

	private int generateList(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerLevel dimensionLevel = DimensionArgument.getDimension(ctx, "dimension");
		ResourceLocation dimension = dimensionLevel.dimension().location();
		//Get list of Chunk Loader positions for the dimension
		List<BlockPos> positions = ChunkData.get(dimensionLevel).generateList(dimension);

		if (positions.isEmpty()) {
			ctx.getSource().sendSuccess(
					new TranslatableComponent("chunkymcchunkface.command.list.empty",
							new TextComponent(dimension.toString()).withStyle(ChatFormatting.RED)), true);
		} else {
			//Convert position list to a formatted string
			MutableComponent formattedComponent = new TextComponent("\n");
			Component component = new TextComponent(", ").withStyle(ChatFormatting.WHITE);
			Component quote = new TextComponent("\"").withStyle(ChatFormatting.WHITE);
			for (int i = 0; i < positions.size(); i++) {
				BlockPos pos = positions.get(i);
				MutableComponent position = ComponentUtils.wrapInSquareBrackets(new TextComponent(pos.toShortString())).withStyle((style) ->
						style.withColor(ChatFormatting.GOLD)
								.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip"))));
				formattedComponent.append(position);
				if (i < positions.size() - 1)
					formattedComponent.append(component);
			}

			ctx.getSource().sendSuccess(
					new TranslatableComponent("chunkymcchunkface.command.list",
							new TextComponent(dimension.toString()).withStyle(ChatFormatting.GOLD))
							.append(formattedComponent), true);
		}
		return 0;
	}
}
