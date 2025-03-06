package me.paulf.fairylights.data;

import com.google.common.collect.ImmutableList;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.fabricators_of_create.porting_lib.data.ModdedBlockLootSubProvider;
import io.github.fabricators_of_create.porting_lib.data.ModdedLootTableProvider;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.mixin.fabric.LootTableProviderAccessor;
import me.paulf.fairylights.mixin.fabric.ShapedRecipeAccessor;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.components.FLComponents;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class DataGatherer implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
        var pack = gen.createPack();
        pack.addProvider(RecipeGenerator::new);
        pack.addProvider(LootTableGenerator::new);
    }

    static class RecipeGenerator extends FabricRecipeProvider {
        public RecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void buildRecipes(final RecipeOutput consumer) {
            var components = DataComponentMap.builder()
                .set(FLComponents.STYLED_STRING, new StyledString())
                .build();
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FLItems.LETTER_BUNTING.get())
                .pattern("I-I")
                .pattern("PBF")
                .define('I', ConventionalItemTags.IRON_INGOTS)
                .define('-', ConventionalItemTags.STRINGS)
                .define('P', Items.PAPER)
                .define('B', Items.INK_SAC)
                .define('F', ConventionalItemTags.FEATHERS)
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
                .save(addNbt(consumer, components));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,FLItems.GARLAND.get(), 2)
                .pattern("I-I")
                .define('I', ConventionalItemTags.IRON_INGOTS)
                .define('-', Items.VINE)
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                .unlockedBy("has_vine", has(Items.VINE))
                .save(consumer);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,FLItems.OIL_LANTERN.get(), 4)
                .pattern(" I ")
                .pattern("STS")
                .pattern("IGI")
                .define('I', ConventionalItemTags.IRON_INGOTS)
                .define('S', Items.STICK)
                .define('T', Items.TORCH)
                .define('G', ConventionalItemTags.GLASS_PANES_COLORLESS)
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                .unlockedBy("has_torch", has(Items.TORCH))
                .save(consumer);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,FLItems.CANDLE_LANTERN.get(), 4)
                .pattern(" I ")
                .pattern("GTG")
                .pattern("IGI")
                .define('I', ConventionalItemTags.IRON_INGOTS)
                .define('G', ConventionalItemTags.GOLD_NUGGETS)
                .define('T', Items.TORCH)
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                .unlockedBy("has_torch", has(Items.TORCH))
                .save(consumer);
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,FLItems.INCANDESCENT_LIGHT.get(), 4)
                .pattern(" I ")
                .pattern("ITI")
                .pattern(" G ")
                .define('I', ConventionalItemTags.IRON_INGOTS)
                .define('G', ConventionalItemTags.GLASS_PANES_COLORLESS)
                .define('T', Items.TORCH)
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                .unlockedBy("has_torch", has(Items.TORCH))
                .save(consumer);
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.HANGING_LIGHTS.get())
                .unlockedBy("has_lights", has(FLCraftingRecipes.LIGHTS))
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "hanging_lights"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.HANGING_LIGHTS_AUGMENTATION.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "hanging_lights_augmentation"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.TINSEL_GARLAND.get())
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                .unlockedBy("has_string", has(ConventionalItemTags.STRINGS))
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "tinsel_garland"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT_BUNTING.get())
                .unlockedBy("has_pennants", has(FLCraftingRecipes.PENNANTS))
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "pennant_bunting"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.PENNANT_BUNTING_AUGMENTATION.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "pennant_bunting_augmentation"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.EDIT_COLOR.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "edit_color"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.COPY_COLOR.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "copy_color"));
            this.pennantRecipe(FLCraftingRecipes.TRIANGLE_PENNANT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "triangle_pennant"));
            this.pennantRecipe(FLCraftingRecipes.SPEARHEAD_PENNANT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "spearhead_pennant"));
            this.pennantRecipe(FLCraftingRecipes.SWALLOWTAIL_PENNANT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "swallowtail_pennant"));
            this.pennantRecipe(FLCraftingRecipes.SQUARE_PENNANT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "square_pennant"));
            this.lightRecipe(FLCraftingRecipes.FAIRY_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "fairy_light"));
            this.lightRecipe(FLCraftingRecipes.PAPER_LANTERN.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "paper_lantern"));
            this.lightRecipe(FLCraftingRecipes.ORB_LANTERN.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "orb_lantern"));
            this.lightRecipe(FLCraftingRecipes.FLOWER_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "flower_light"));
            this.lightRecipe(FLCraftingRecipes.CANDLE_LANTERN_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "candle_lantern_light"));
            this.lightRecipe(FLCraftingRecipes.OIL_LANTERN_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "oil_lantern_light"));
            this.lightRecipe(FLCraftingRecipes.JACK_O_LANTERN.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "jack_o_lantern"));
            this.lightRecipe(FLCraftingRecipes.SKULL_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "skull_light"));
            this.lightRecipe(FLCraftingRecipes.GHOST_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "ghost_light"));
            this.lightRecipe(FLCraftingRecipes.SPIDER_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "spider_light"));
            this.lightRecipe(FLCraftingRecipes.WITCH_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "witch_light"));
            this.lightRecipe(FLCraftingRecipes.SNOWFLAKE_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "snowflake_light"));
            this.lightRecipe(FLCraftingRecipes.HEART_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "heart_light"));
            this.lightRecipe(FLCraftingRecipes.MOON_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "moon_light"));
            this.lightRecipe(FLCraftingRecipes.STAR_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "star_light"));
            this.lightRecipe(FLCraftingRecipes.ICICLE_LIGHTS.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "icicle_lights"));
            this.lightRecipe(FLCraftingRecipes.METEOR_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "meteor_light"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.LIGHT_TWINKLE.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "light_twinkle"));
            GenericRecipeBuilder.customRecipe(FLCraftingRecipes.COLOR_CHANGING_LIGHT.get())
                .build(consumer, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "color_changing_light"));
        }

        GenericRecipeBuilder lightRecipe(final RecipeSerializer<?> serializer) {
            return GenericRecipeBuilder.customRecipe(serializer)
                .unlockedBy("has_iron", has(ConventionalItemTags.IRON_INGOTS))
                .unlockedBy("has_dye", has(ConventionalItemTags.DYES));
        }

        GenericRecipeBuilder pennantRecipe(final RecipeSerializer<?> serializer) {
            return GenericRecipeBuilder.customRecipe(serializer)
                .unlockedBy("has_paper", has(Items.PAPER))
                .unlockedBy("has_string", has(ConventionalItemTags.STRINGS));
        }
    }

    static class LootTableGenerator extends ModdedLootTableProvider {

        public LootTableGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, Set.of(), ((LootTableProviderAccessor) VanillaLootTableProvider.create(output, registries)).getSubProviders(), registries);
        }

        @Override
        public List<LootTableProvider.SubProviderEntry> getTables()
        {
            return ImmutableList.of(new LootTableProvider.SubProviderEntry(BlockLootTableGenerator::new, LootContextParamSets.BLOCK));
        }

        @Override
        protected void validate(WritableRegistry<LootTable> registry, ValidationContext ctx, ProblemReporter.Collector collector) {
            // For built-in mod loot tables
            /*for (final ResourceLocation name : Sets.difference(MyBuiltInLootTables.getAll(), map.defineSet())) {
                tracker.addProblem("Missing built-in table: " + name);
            }*/
            registry.forEach(table -> table.validate(ctx));
        }
    }

    static class BlockLootTableGenerator extends ModdedBlockLootSubProvider
    {
        protected BlockLootTableGenerator(HolderLookup.Provider provider)
        {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return StreamSupport.stream(FLBlocks.REG.spliterator(), false).map(RegistrySupplier::get).collect(Collectors.toList());
        }

        @Override
        public void generate()
        {
            this.add(FLBlocks.FASTENER.get(), noDrop());
            this.add(FLBlocks.FAIRY_LIGHT.get(), noDrop());
            this.add(FLBlocks.PAPER_LANTERN.get(), noDrop());
            this.add(FLBlocks.ORB_LANTERN.get(), noDrop());
            this.add(FLBlocks.FLOWER_LIGHT.get(), noDrop());
            this.add(FLBlocks.CANDLE_LANTERN_LIGHT.get(), noDrop());
            this.add(FLBlocks.OIL_LANTERN_LIGHT.get(), noDrop());
            this.add(FLBlocks.JACK_O_LANTERN.get(), noDrop());
            this.add(FLBlocks.SKULL_LIGHT.get(), noDrop());
            this.add(FLBlocks.GHOST_LIGHT.get(), noDrop());
            this.add(FLBlocks.SPIDER_LIGHT.get(), noDrop());
            this.add(FLBlocks.WITCH_LIGHT.get(), noDrop());
            this.add(FLBlocks.SNOWFLAKE_LIGHT.get(), noDrop());
            this.add(FLBlocks.HEART_LIGHT.get(), noDrop());
            this.add(FLBlocks.MOON_LIGHT.get(), noDrop());
            this.add(FLBlocks.STAR_LIGHT.get(), noDrop());
            this.add(FLBlocks.ICICLE_LIGHTS.get(), noDrop());
            this.add(FLBlocks.METEOR_LIGHT.get(), noDrop());
            this.add(FLBlocks.OIL_LANTERN.get(), noDrop());
            this.add(FLBlocks.CANDLE_LANTERN.get(), noDrop());
            this.add(FLBlocks.INCANDESCENT_LIGHT.get(), noDrop());
        }
    }

    static RecipeOutput addNbt(final RecipeOutput consumer, final DataComponentMap components) {
        return new RecipeOutput() {
            @Override
            public void accept(ResourceLocation location, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    var modified = ((ShapedRecipeAccessor) shapedRecipe).getResult().copy();
                    modified.applyComponents(components);

                    consumer.accept(location, new ShapedRecipe(shapedRecipe.getGroup(), shapedRecipe.category(), ((ShapedRecipeAccessor) shapedRecipe).getPattern(),
                        modified,
                        shapedRecipe.showNotification()), advancement
                    );
                } else consumer.accept(location, recipe, advancement);
            }

            @Override
            public Advancement.Builder advancement() {
                return consumer.advancement();
            }
        };
    }
}
