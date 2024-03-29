package com.mrbysco.chunkymcchunkface.datagen.data;

import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class ChunkyRecipeProvider extends RecipeProvider {
	public ChunkyRecipeProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ChunkyRegistry.CHUNK_LOADER.get())
				.define('G', Items.GOLD_BLOCK)
				.define('E', Tags.Items.NETHER_STARS)
				.define('O', Tags.Items.OBSIDIAN)
				.define('W', Tags.Items.GLASS)
				.pattern("GOG").pattern("WEW").pattern("GOG").unlockedBy("has_ender_pearl",
						has(Tags.Items.ENDER_PEARLS)).save(recipeConsumer);
	}
}
