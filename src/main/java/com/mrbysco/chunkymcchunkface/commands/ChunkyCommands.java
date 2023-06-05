package com.mrbysco.chunkymcchunkface.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.chunkymcchunkface.ChunkyMcChunkFace;
import com.mrbysco.chunkymcchunkface.blocks.entity.ChunkLoaderBlockEntity;
import com.mrbysco.chunkymcchunkface.data.ChunkData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
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
								.executes(ctx -> generateList(ctx, false))
								.then(Commands.literal("enabled")
										.executes(ctx -> generateList(ctx, true))))
				)
				.then(Commands.literal("disable")
						.then(Commands.argument("dimension", DimensionArgument.dimension())
								.then(Commands.argument("position", Vec3Argument.vec3())
										.suggests((context, builder) -> {
											ServerLevel dimensionLevel = DimensionArgument.getDimension(context, "dimension");
											return SharedSuggestionProvider.suggest(getActivePositions(dimensionLevel), builder);
										})
										.executes(ChunkyCommands::disableChunkLoader)
								))
				)
				.then(Commands.literal("disable_all")
						.then(Commands.argument("dimension", DimensionArgument.dimension())
								.executes(ChunkyCommands::disableAllChunkLoaders))
				);

		dispatcher.register(root);
	}

	/**
	 * Get a list of all active Chunk Loader positions in a dimension
	 *
	 * @param dimensionLevel The dimension to get the positions from
	 * @return A list of all active Chunk Loader positions
	 */
	protected static List<String> getActivePositions(ServerLevel dimensionLevel) {
		ResourceLocation dimension = dimensionLevel.dimension().location();
		ChunkData data = ChunkData.get(dimensionLevel);
		List<BlockPos> positions = data.getActivePositions(dimensionLevel, data.generateList(dimension));
		return positions.stream().map(pos -> pos.getX() + " " + pos.getY() + " " + pos.getZ()).toList();
	}

	/**
	 * Generate a list of Chunk Loader positions for a dimension
	 *
	 * @param ctx         The command context
	 * @param enabledOnly Whether to only get enabled Chunk Loader positions
	 * @return The result of the command
	 * @throws CommandSyntaxException If the command syntax is invalid
	 */
	private int generateList(CommandContext<CommandSourceStack> ctx, boolean enabledOnly) throws CommandSyntaxException {
		ServerLevel dimensionLevel = DimensionArgument.getDimension(ctx, "dimension");
		ResourceLocation dimension = dimensionLevel.dimension().location();
		//Get list of Chunk Loader positions for the dimension
		ChunkData data = ChunkData.get(dimensionLevel);
		List<BlockPos> positions = data.generateList(dimension);
		if (enabledOnly) {
			positions = data.getActivePositions(dimensionLevel, positions);
		}

		if (positions.isEmpty()) {
			ctx.getSource().sendSuccess(
					Component.translatable("chunkymcchunkface.command.list.empty",
							Component.literal(dimension.toString()).withStyle(ChatFormatting.RED)), true);
		} else {
			//Convert position list to a formatted string
			MutableComponent formattedComponent = Component.literal("\n");
			Component component = Component.literal(", ").withStyle(ChatFormatting.WHITE);
			for (int i = 0; i < positions.size(); i++) {
				BlockPos pos = positions.get(i);
				MutableComponent position = ComponentUtils.wrapInSquareBrackets(Component.literal(pos.toShortString())).withStyle((style) ->
						style.withColor(ChatFormatting.GOLD)
								.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ()))
								.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip"))));
				formattedComponent.append(position);
				if (i < positions.size() - 1)
					formattedComponent.append(component);
			}

			ctx.getSource().sendSuccess(
					Component.translatable("chunkymcchunkface.command.list",
									Component.literal(dimension.toString()).withStyle(ChatFormatting.GOLD))
							.append(formattedComponent), true);
		}
		return 0;
	}

	/**
	 * Disable a Chunk Loader at a position
	 *
	 * @param ctx The command context
	 * @return The result of the command
	 * @throws CommandSyntaxException If the command syntax is invalid
	 */
	private static int disableChunkLoader(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerLevel dimensionLevel = DimensionArgument.getDimension(ctx, "dimension");
		Vec3 pos = Vec3Argument.getVec3(ctx, "position");
		if (dimensionLevel.getBlockEntity(new BlockPos(pos)) instanceof ChunkLoaderBlockEntity loader) {
			loader.disableChunkLoaderState();
			loader.disableChunkLoader();
			ctx.getSource().sendSuccess(Component.translatable("chunkymcchunkface.command.disable", pos.x, pos.y, pos.z), true);
		} else {
			ctx.getSource().sendSuccess(Component.translatable("chunkymcchunkface.command.disable.error", pos.x, pos.y, pos.z), true);
		}
		return 0;
	}

	/**
	 * Disable all Chunk Loaders in a dimension
	 *
	 * @param ctx The command context
	 * @return The result of the command
	 * @throws CommandSyntaxException If the command syntax is invalid
	 */
	private static int disableAllChunkLoaders(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ServerLevel dimensionLevel = DimensionArgument.getDimension(ctx, "dimension");
		ResourceLocation dimension = dimensionLevel.dimension().location();
		ChunkData data = ChunkData.get(dimensionLevel);
		List<BlockPos> positions = data.getActivePositions(dimensionLevel, data.generateList(dimension));
		MutableComponent dimensionComponent = Component.literal(dimension.toString()).withStyle(ChatFormatting.GOLD);
		if (positions.isEmpty()) {
			ctx.getSource().sendSuccess(Component.translatable("chunkymcchunkface.command.disableall.empty", dimensionComponent), true);
		} else {
			for (BlockPos pos : positions) {
				if (dimensionLevel.getBlockEntity(new BlockPos(pos)) instanceof ChunkLoaderBlockEntity loader) {
					loader.disableChunkLoaderState();
					loader.disableChunkLoader();
				}
			}
			ctx.getSource().sendSuccess(Component.translatable("chunkymcchunkface.command.disableall", dimensionComponent), true);
		}

		return 0;
	}
}
