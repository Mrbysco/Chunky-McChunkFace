package com.mrbysco.chunkymcchunkface.datagen.data;

import com.mrbysco.chunkymcchunkface.registry.ChunkyRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ChunkyRecipeProvider extends RecipeProvider {
	public ChunkyRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		super(provider, recipeOutput);
	}

	@Override
	protected void buildRecipes() {
		shaped(RecipeCategory.REDSTONE, ChunkyRegistry.CHUNK_LOADER.get())
				.define('G', Items.GOLD_BLOCK)
				.define('E', Tags.Items.NETHER_STARS)
				.define('O', Tags.Items.OBSIDIANS)
				.define('W', Tags.Items.GLASS_BLOCKS)
				.pattern("GOG").pattern("WEW").pattern("GOG").unlockedBy("has_ender_pearl",
						has(Tags.Items.ENDER_PEARLS)).save(output);
	}

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			return new ChunkyRecipeProvider(provider, recipeOutput);
		}

		@Override
		public String getName() {
			return "Chunky McChunkFace Recipes";
		}
	}
}
