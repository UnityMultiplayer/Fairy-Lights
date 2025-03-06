package me.paulf.fairylights.server.item.crafting;

import com.google.common.collect.ImmutableList;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.DyeableItem;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.components.FLComponents;
import me.paulf.fairylights.server.item.components.ModifiableDataComponentMap;
import me.paulf.fairylights.server.string.StringTypes;
import me.paulf.fairylights.util.Blender;
import me.paulf.fairylights.util.OreDictUtils;
import me.paulf.fairylights.util.Utils;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import me.paulf.fairylights.util.crafting.GenericRecipeBuilder;
import me.paulf.fairylights.util.crafting.ingredient.*;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class FLCraftingRecipes {
    private FLCraftingRecipes() {}

    public static final DeferredRegister<RecipeSerializer<?>> REG = DeferredRegister.create(FairyLights.ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> HANGING_LIGHTS = REG.register("crafting_special_hanging_lights", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createHangingLights));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> HANGING_LIGHTS_AUGMENTATION = REG.register("crafting_special_hanging_lights_augmentation", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createHangingLightsAugmentation));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> TINSEL_GARLAND = REG.register("crafting_special_tinsel_garland", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createTinselGarland));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> PENNANT_BUNTING = REG.register("crafting_special_pennant_bunting", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createPennantBunting));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> PENNANT_BUNTING_AUGMENTATION = REG.register("crafting_special_pennant_bunting_augmentation", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createPennantBuntingAugmentation));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> TRIANGLE_PENNANT = REG.register("crafting_special_triangle_pennant", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createTrianglePennant));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> SPEARHEAD_PENNANT = REG.register("crafting_special_spearhead_pennant", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createSpearheadPennant));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> SWALLOWTAIL_PENNANT = REG.register("crafting_special_swallowtail_pennant", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createSwallowtailPennant));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> SQUARE_PENNANT = REG.register("crafting_special_square_pennant", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createSquarePennant));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> FAIRY_LIGHT = REG.register("crafting_special_fairy_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createFairyLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> PAPER_LANTERN = REG.register("crafting_special_paper_lantern", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createPaperLantern));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> ORB_LANTERN = REG.register("crafting_special_orb_lantern", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createOrbLantern));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> FLOWER_LIGHT = REG.register("crafting_special_flower_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createFlowerLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> CANDLE_LANTERN_LIGHT = REG.register("crafting_special_candle_lantern_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createCandleLanternLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> OIL_LANTERN_LIGHT = REG.register("crafting_special_oil_lantern_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createOilLanternLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> JACK_O_LANTERN = REG.register("crafting_special_jack_o_lantern", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createJackOLantern));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> SKULL_LIGHT = REG.register("crafting_special_skull_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createSkullLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> GHOST_LIGHT = REG.register("crafting_special_ghost_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createGhostLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> SPIDER_LIGHT = REG.register("crafting_special_spider_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createSpiderLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> WITCH_LIGHT = REG.register("crafting_special_witch_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createWitchLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> SNOWFLAKE_LIGHT = REG.register("crafting_special_snowflake_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createSnowflakeLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> HEART_LIGHT = REG.register("crafting_special_heart_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createHeartLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> MOON_LIGHT = REG.register("crafting_special_moon_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createMoonLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> STAR_LIGHT = REG.register("crafting_special_star_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createStarLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> ICICLE_LIGHTS = REG.register("crafting_special_icicle_lights", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createIcicleLights));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> METEOR_LIGHT = REG.register("crafting_special_meteor_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createMeteorLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> LIGHT_TWINKLE = REG.register("crafting_special_light_twinkle", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createLightTwinkle));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> COLOR_CHANGING_LIGHT = REG.register("crafting_special_color_changing_light", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createColorChangingLight));

    public static final RegistrySupplier<RecipeSerializer<GenericRecipe>> EDIT_COLOR = REG.register("crafting_special_edit_color", () -> new SimpleCraftingRecipeSerializer<>(FLCraftingRecipes::createDyeColor));

    public static final RegistrySupplier<? extends RecipeSerializer<CustomRecipe>> COPY_COLOR = REG.register("crafting_special_copy_color", () -> new SimpleCraftingRecipeSerializer<>(CopyColorRecipe::new));

    public static final TagKey<Item> LIGHTS = TagKey.create(Registries.ITEM, ResourceLocation.parse(FairyLights.ID + ":lights"));

    public static final TagKey<Item> TWINKLING_LIGHTS = TagKey.create(Registries.ITEM, ResourceLocation.parse(FairyLights.ID + ":twinkling_lights"));

    public static final TagKey<Item> PENNANTS = TagKey.create(Registries.ITEM, ResourceLocation.parse(FairyLights.ID + ":pennants"));

    public static final TagKey<Item> DYEABLE = TagKey.create(Registries.ITEM, ResourceLocation.parse(FairyLights.ID + ":dyeable"));

    public static final TagKey<Item> DYEABLE_LIGHTS = TagKey.create(Registries.ITEM, ResourceLocation.parse(FairyLights.ID + ":dyeable_lights"));

    public static final RegularIngredient DYE_SUBTYPE_INGREDIENT = new BasicRegularIngredient(LazyTagIngredient.of(Tags.Items.DYES)) {
        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            return DyeableItem.getDyeColor(output).map(dye -> ImmutableList.of(OreDictUtils.getDyes(dye))).orElse(ImmutableList.of());
        }

        @Override
        public boolean dictatesOutputType() {
            return true;
        }

        @Override
        public void matched(ItemStack ingredient, ModifiableDataComponentMap components) {
            components.set(FLComponents.COLOR, OreDictUtils.getDyeColor(ingredient).getTextureDiffuseColor());
        }
    };

    private static GenericRecipe createDyeColor(final CraftingBookCategory craftingBookCategory) {
        return new GenericRecipeBuilder(EDIT_COLOR)
            .withShape("I")
            .withIngredient('I', DYEABLE).withOutput('I')
            .withAuxiliaryIngredient(new BasicAuxiliaryIngredient<Blender>(LazyTagIngredient.of(Tags.Items.DYES), true, 8) {
                @Override
                public Blender accumulator() {
                    return new Blender();
                }

                @Override
                public void consume(final Blender data, final ItemStack ingredient) {
                    data.add(DyeableItem.getColor(OreDictUtils.getDyeColor(ingredient)));
                }

                @Override
                public boolean finish(Blender data, ModifiableDataComponentMap components) {
                    components.set(FLComponents.COLOR, data.blend());
                    return false;
                }
            })
            .build();
    }

    private static GenericRecipe createLightTwinkle(final CraftingBookCategory craftingBookCategory) {
        return new GenericRecipeBuilder(LIGHT_TWINKLE)
            .withShape("L")
            .withIngredient('L', TWINKLING_LIGHTS).withOutput('L')
            .withAuxiliaryIngredient(new InertBasicAuxiliaryIngredient(LazyTagIngredient.of(Tags.Items.DUSTS_GLOWSTONE), true, 1) {
                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    return output.has(FLComponents.TWINKLES) && output.get(FLComponents.TWINKLES) ? super.getInput(output) : ImmutableList.of();
                }

                @Override
                public void present(final ModifiableDataComponentMap nbt) {
                    nbt.set(FLComponents.TWINKLES, true);
                }

                @Override
                public void absent(final ModifiableDataComponentMap nbt) {
                    nbt.set(FLComponents.TWINKLES, false);
                }

                @Override
                public void addTooltip(final List<Component> tooltip) {
                    super.addTooltip(tooltip);
                    tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.twinkling_lights.glowstone"));
                }
            })
            .build();
    }

    private static GenericRecipe createColorChangingLight(final CraftingBookCategory craftingBookCategory) {
        return new GenericRecipeBuilder(COLOR_CHANGING_LIGHT)
            .withShape("IG")
            .withIngredient('I', DYEABLE_LIGHTS).withOutput('I')
            .withIngredient('G', Tags.Items.NUGGETS_GOLD)
            .withAuxiliaryIngredient(new BasicAuxiliaryIngredient<ListTag>(LazyTagIngredient.of(Tags.Items.DYES), true, 8) {
                @Override
                public ListTag accumulator() {
                    return new ListTag();
                }

                @Override
                public void consume(final ListTag data, final ItemStack ingredient) {
                    data.add(IntTag.valueOf(DyeableItem.getColor(OreDictUtils.getDyeColor(ingredient))));
                }

                @Override
                public boolean finish(final ListTag data, final ModifiableDataComponentMap nbt) {
                    if (!data.isEmpty()) {
                        if (nbt.has(FLComponents.COLOR)) {
                            data.add(0, IntTag.valueOf(nbt.get(FLComponents.COLOR)));
                            nbt.remove(FLComponents.COLOR);
                        }
                        nbt.set(FLComponents.COLORS, data.stream().map(s -> ((IntTag) s).getAsInt()).toList());
                    }
                    return false;
                }
            })
            .build();
    }

    private static GenericRecipe createHangingLights(CraftingBookCategory craftingBookCategory) {
        return new GenericRecipeBuilder(HANGING_LIGHTS, FLItems.HANGING_LIGHTS.get())
            .withShape("I-I")
            .withIngredient('I', ConventionalItemTags.IRON_INGOTS)
            .withIngredient('-', ConventionalItemTags.STRINGS)
            .withAuxiliaryIngredient(new LightIngredient(true))
            .withAuxiliaryIngredient(new InertBasicAuxiliaryIngredient(LazyTagIngredient.of(Tags.Items.DYES_WHITE), false, 1) {
                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    return output.has(FLComponents.STRING_TYPE) && output.get(FLComponents.STRING_TYPE) == StringTypes.WHITE_STRING.get() ? super.getInput(output) : ImmutableList.of();
                }

                @Override
                public void present(final ModifiableDataComponentMap nbt) {
                    nbt.set(FLComponents.STRING_TYPE, StringTypes.WHITE_STRING.get());
                }

                @Override
                public void absent(final ModifiableDataComponentMap nbt) {
                    nbt.set(FLComponents.STRING_TYPE, StringTypes.BLACK_STRING.get());
                }

                @Override
                public void addTooltip(final List<Component> tooltip) {
                    super.addTooltip(tooltip);
                    tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.hangingLights.string"));
                }
            })
            .build();
    }

    /*
     *  The JEI shown recipe is adding glowstone, eventually I should allow a recipe to provide a number of
     *  different recipe layouts the the input ingredients can be generated for so I could show applying a
     *  new light pattern as well.
     */
    private static GenericRecipe createHangingLightsAugmentation(final CraftingBookCategory craftingBookCategory) {
        return new GenericRecipeBuilder(HANGING_LIGHTS_AUGMENTATION, FLItems.HANGING_LIGHTS.get())
            .withShape("F")
            .withIngredient('F', new BasicRegularIngredient(Ingredient.of(FLItems.HANGING_LIGHTS.get())) {
                @Override
                public ImmutableList<ItemStack> getInputs() {
                    return Arrays.stream(this.ingredient.getItems())
                        .map(ItemStack::copy)
                        .flatMap(stack -> {
                            return makeHangingLightsExamples(stack).stream();
                        }).collect(ImmutableList.toImmutableList());
                }

                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    final ItemStack stack = output.copy();
                    if (stack.getComponents().isEmpty()) {
                        return ImmutableList.of();
                    }
                    stack.setCount(1);
                    return ImmutableList.of(ImmutableList.of(stack));
                }

                @Override
                public void matched(final ItemStack ingredient, final ModifiableDataComponentMap nbt) {
                    if (ingredient.getComponents() != null) {
                        nbt.merge(ingredient.getComponents());
                    }
                }
            })
            .withAuxiliaryIngredient(new LightIngredient(true) {
                @Override
                public ImmutableList<ItemStack> getInputs() {
                    return ImmutableList.of();
                }

                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    return ImmutableList.of();
                }
            })
            .build();
    }

    private static ImmutableList<ItemStack> makeHangingLightsExamples(final ItemStack stack) {
        return ImmutableList.of(
            makeHangingLights(stack, DyeColor.CYAN, DyeColor.MAGENTA, DyeColor.CYAN, DyeColor.WHITE),
            makeHangingLights(stack, DyeColor.CYAN, DyeColor.LIGHT_BLUE, DyeColor.CYAN, DyeColor.LIGHT_BLUE),
            makeHangingLights(stack, DyeColor.LIGHT_GRAY, DyeColor.PINK, DyeColor.CYAN, DyeColor.GREEN),
            makeHangingLights(stack, DyeColor.LIGHT_GRAY, DyeColor.PURPLE, DyeColor.LIGHT_GRAY, DyeColor.GREEN),
            makeHangingLights(stack, DyeColor.CYAN, DyeColor.YELLOW, DyeColor.CYAN, DyeColor.PURPLE)
        );
    }

    public static ItemStack makeHangingLights(final ItemStack base, final DyeColor... colors) {
        final ItemStack stack = base.copy();
        final List<ItemStack> lights = new ArrayList<>();
        for (final DyeColor color : colors) {
            lights.add(DyeableItem.setColor(new ItemStack(FLItems.FAIRY_LIGHT.get()), color));
        }
        stack.set(FLComponents.PATTERN, lights);
        stack.set(FLComponents.STRING_TYPE, StringTypes.BLACK_STRING.get());
        return stack;
    }

    private static GenericRecipe createTinselGarland(final CraftingBookCategory craftingBookCategory) {
        return new GenericRecipeBuilder(TINSEL_GARLAND, FLItems.TINSEL.get())
            .withShape(" P ", "I-I", " D ")
            .withIngredient('P', Items.PAPER)
            .withIngredient('I', ConventionalItemTags.IRON_INGOTS)
            .withIngredient('-', ConventionalItemTags.STRINGS)
            .withIngredient('D', DYE_SUBTYPE_INGREDIENT)
            .build();
    }

    private static GenericRecipe createPennantBunting(final CraftingBookCategory craftingBookCategory) {
        return new GenericRecipeBuilder(PENNANT_BUNTING, FLItems.PENNANT_BUNTING.get())
            .withShape("I-I")
            .withIngredient('I', ConventionalItemTags.IRON_INGOTS)
            .withIngredient('-', ConventionalItemTags.STRINGS)
            .withAuxiliaryIngredient(new PennantIngredient())
            .build();
    }

    private static GenericRecipe createPennantBuntingAugmentation(final CraftingBookCategory craftingBookCategory) {
        return new GenericRecipeBuilder(PENNANT_BUNTING_AUGMENTATION, FLItems.PENNANT_BUNTING.get())
            .withShape("B")
            .withIngredient('B', new BasicRegularIngredient(Ingredient.of(FLItems.PENNANT_BUNTING.get())) {
                @Override
                public ImmutableList<ItemStack> getInputs() {
                    return Arrays.stream(this.ingredient.getItems())
                        .map(ItemStack::copy)
                        .flatMap(stack -> {
                            return makePennantExamples(stack).stream();
                        }).collect(ImmutableList.toImmutableList());
                }

                @Override
                public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                    if (output.getComponents().isEmpty()) {
                        return ImmutableList.of();
                    }
                    return ImmutableList.of(makePennantExamples(output));
                }

                @Override
                public void matched(final ItemStack ingredient, final ModifiableDataComponentMap nbt) {
                    final DataComponentMap compound = ingredient.getComponents();
                    if (compound != null && !compound.isEmpty()) {
                        nbt.merge(compound);
                    }
                }
            })
            .withAuxiliaryIngredient(new PennantIngredient())
            .build();
    }

    private static ImmutableList<ItemStack> makePennantExamples(final ItemStack stack) {
        return ImmutableList.of(
            makePennant(stack, DyeColor.BLUE, DyeColor.YELLOW, DyeColor.RED),
            makePennant(stack, DyeColor.PINK, DyeColor.LIGHT_BLUE),
            makePennant(stack, DyeColor.ORANGE, DyeColor.WHITE),
            makePennant(stack, DyeColor.LIME, DyeColor.YELLOW)
        );
    }

    public static ItemStack makePennant(final ItemStack base, final DyeColor... colors) {
        final ItemStack stack = base.copy();
        final List<ItemStack> pennants = new ArrayList<>();
        for (final DyeColor color : colors) {
            final ItemStack pennant = new ItemStack(FLItems.TRIANGLE_PENNANT.get());
            DyeableItem.setColor(pennant, color);
            pennants.add(pennant);
        }
        stack.set(FLComponents.PATTERN, pennants);
        stack.set(FLComponents.STYLED_STRING, new StyledString());
        return stack;
    }

    private static GenericRecipe createPennant(final Supplier<RecipeSerializer<GenericRecipe>> serializer, final Item item, final String pattern) {
        return new GenericRecipeBuilder(serializer, item)
            .withShape("- -", "PDP", pattern)
            .withIngredient('P', Items.PAPER)
            .withIngredient('-', ConventionalItemTags.STRINGS)
            .withIngredient('D', DYE_SUBTYPE_INGREDIENT)
            .build();
    }

    private static GenericRecipe createTrianglePennant(final CraftingBookCategory craftingBookCategory) {
        return createPennant(TRIANGLE_PENNANT, FLItems.TRIANGLE_PENNANT.get(), " P ");
    }

    private static GenericRecipe createSpearheadPennant(final CraftingBookCategory craftingBookCategory) {
        return createPennant(SPEARHEAD_PENNANT, FLItems.SPEARHEAD_PENNANT.get(), " PP");
    }

    private static GenericRecipe createSwallowtailPennant(final CraftingBookCategory craftingBookCategory) {
        return createPennant(SWALLOWTAIL_PENNANT, FLItems.SWALLOWTAIL_PENNANT.get(), "P P");
    }

    private static GenericRecipe createSquarePennant(final CraftingBookCategory craftingBookCategory) {
        return createPennant(SQUARE_PENNANT, FLItems.SQUARE_PENNANT.get(), "PPP");
    }

    private static GenericRecipe createFairyLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(FAIRY_LIGHT, FLItems.FAIRY_LIGHT, b -> b
            .withShape(" I ", "IDI", " G ")
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
        );
    }

    private static GenericRecipe createPaperLantern(final CraftingBookCategory craftingBookCategory) {
        return createLight(PAPER_LANTERN, FLItems.PAPER_LANTERN, b -> b
            .withShape(" I ", "PDP", "PPP")
            .withIngredient('P', Items.PAPER)
        );
    }

    private static GenericRecipe createOrbLantern(final CraftingBookCategory craftingBookCategory) {
        return createLight(ORB_LANTERN, FLItems.ORB_LANTERN, b -> b
            .withShape(" I ", "SDS", " W ")
            .withIngredient('S', ConventionalItemTags.STRINGS)
            .withIngredient('W', Items.WHITE_WOOL)
        );
    }

    private static GenericRecipe createFlowerLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(FLOWER_LIGHT, FLItems.FLOWER_LIGHT, b -> b
            .withShape(" I ", "RDB", " Y ")
            .withIngredient('R', Items.POPPY)
            .withIngredient('Y', Items.DANDELION)
            .withIngredient('B', Items.BLUE_ORCHID)
        );
    }

    private static GenericRecipe createCandleLanternLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(CANDLE_LANTERN_LIGHT, FLItems.CANDLE_LANTERN_LIGHT, b -> b
            .withShape(" I ", "GDG", "IGI")
            .withIngredient('G', Tags.Items.NUGGETS_GOLD)
        );
    }

    private static GenericRecipe createOilLanternLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(OIL_LANTERN_LIGHT, FLItems.OIL_LANTERN_LIGHT, b -> b
            .withShape(" I ", "SDS", "IGI")
            .withIngredient('S', Items.STICK)
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
        );
    }

    private static GenericRecipe createJackOLantern(final CraftingBookCategory craftingBookCategory) {
        return createLight(JACK_O_LANTERN, FLItems.JACK_O_LANTERN, b -> b
            .withShape(" I ", "SDS", "GPG")
            .withIngredient('S', ItemTags.WOODEN_SLABS)
            .withIngredient('G', Items.TORCH)
            .withIngredient('P', Items.JACK_O_LANTERN)
        );
    }

    private static GenericRecipe createSkullLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(SKULL_LIGHT, FLItems.SKULL_LIGHT, b -> b
            .withShape(" I ", "IDI", " B ")
            .withIngredient('B', Tags.Items.BONES)
        );
    }

    private static GenericRecipe createGhostLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(GHOST_LIGHT, FLItems.GHOST_LIGHT, b -> b
            .withShape(" I ", "PDP", "IGI")
            .withIngredient('P', Items.PAPER)
            .withIngredient('G', Items.WHITE_STAINED_GLASS_PANE)
        );
    }

    private static GenericRecipe createSpiderLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(SPIDER_LIGHT, FLItems.SPIDER_LIGHT, b -> b
            .withShape(" I ", "WDW", "SES")
            .withIngredient('W', Items.COBWEB)
            .withIngredient('S', ConventionalItemTags.STRINGS)
            .withIngredient('E', Items.SPIDER_EYE)
        );
    }

    private static GenericRecipe createWitchLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(WITCH_LIGHT, FLItems.WITCH_LIGHT, b -> b
            .withShape(" I ", "BDW", " S ")
            .withIngredient('B', Items.GLASS_BOTTLE)
            .withIngredient('W', Items.WHEAT)
            .withIngredient('S', Items.STICK)
        );
    }

    private static GenericRecipe createSnowflakeLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(SNOWFLAKE_LIGHT, FLItems.SNOWFLAKE_LIGHT, b -> b
            .withShape(" I ", "SDS", " G ")
            .withIngredient('S', Items.SNOWBALL)
            .withIngredient('G', Items.WHITE_STAINED_GLASS_PANE)
        );
    }

    private static GenericRecipe createHeartLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(HEART_LIGHT, FLItems.HEART_LIGHT, b -> b
            .withShape(" I ", "IDI", " G ")
            .withIngredient('G', Items.RED_STAINED_GLASS_PANE)
        );
    }

    private static GenericRecipe createMoonLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(MOON_LIGHT, FLItems.MOON_LIGHT, b -> b
            .withShape(" I ", "GDG", " C ")
            .withIngredient('G', Items.WHITE_STAINED_GLASS_PANE)
            .withIngredient('C', Items.CLOCK)
        );
    }


    private static GenericRecipe createStarLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(STAR_LIGHT, FLItems.STAR_LIGHT, b -> b
            .withShape(" I ", "PDP", " G ")
            .withIngredient('P', Items.WHITE_STAINED_GLASS_PANE)
            .withIngredient('G', Tags.Items.NUGGETS_GOLD)
        );
    }

    private static GenericRecipe createIcicleLights(final CraftingBookCategory craftingBookCategory) {
        return createLight(ICICLE_LIGHTS, FLItems.ICICLE_LIGHTS, b -> b
            .withShape(" I ", "GDG", " B ")
            .withIngredient('G', Tags.Items.GLASS_PANES_COLORLESS)
            .withIngredient('B', Items.WATER_BUCKET)
        );
    }

    private static GenericRecipe createMeteorLight(final CraftingBookCategory craftingBookCategory) {
        return createLight(METEOR_LIGHT, FLItems.METEOR_LIGHT, b -> b
            .withShape(" I ", "GDG", "IPI")
            .withIngredient('G', Tags.Items.DUSTS_GLOWSTONE)
            .withIngredient('P', Items.PAPER)
        );
    }

    private static GenericRecipe createLight(final Supplier<? extends RecipeSerializer<GenericRecipe>> serializer, final Supplier<? extends Item> variant, final UnaryOperator<GenericRecipeBuilder> recipe) {
        return recipe.apply(new GenericRecipeBuilder(serializer))
            .withIngredient('I', ConventionalItemTags.IRON_INGOTS)
            .withIngredient('D', FLCraftingRecipes.DYE_SUBTYPE_INGREDIENT)
            .withOutput(variant.get(), 4)
            .build();
    }

    private static class LightIngredient extends BasicAuxiliaryIngredient<List<ItemStack>> {
        private LightIngredient(final boolean isRequired) {
            super(LazyTagIngredient.of(LIGHTS), isRequired, 8);
        }

        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            final DataComponentMap compound = output.getComponents();
            if (compound == null || compound.isEmpty()) {
                return ImmutableList.of();
            }
            final List<ItemStack> pattern = compound.get(FLComponents.PATTERN);
            if (pattern.isEmpty()) {
                return ImmutableList.of();
            }
            final ImmutableList.Builder<ImmutableList<ItemStack>> lights = ImmutableList.builder();
            for (int i = 0; i < pattern.size(); i++) {
                lights.add(ImmutableList.of(pattern.get(i)));
            }
            return lights.build();
        }

        @Override
        public boolean dictatesOutputType() {
            return true;
        }

        @Override
        public List<ItemStack> accumulator() {
            return new ArrayList<>();
        }

        @Override
        public void consume(final List<ItemStack> patternList, final ItemStack ingredient) {
            patternList.add(ingredient);
        }

        @Override
        public boolean finish(final List<ItemStack> pattern, final ModifiableDataComponentMap nbt) {
            if (!pattern.isEmpty()) {
                nbt.set(FLComponents.PATTERN, pattern);
            }
            return false;
        }

        @Override
        public void addTooltip(final List<Component> tooltip) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.hangingLights.light"));
        }
    }

    private static class PennantIngredient extends BasicAuxiliaryIngredient<List<ItemStack>> {
        private PennantIngredient() {
            super(LazyTagIngredient.of(PENNANTS), true, 8);
        }

        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            final DataComponentMap compound = output.getComponents();
            if (compound == null || compound.isEmpty()) {
                return ImmutableList.of();
            }
            final List<ItemStack> pattern = compound.get(FLComponents.PATTERN);
            if (pattern.isEmpty()) {
                return ImmutableList.of();
            }
            final ImmutableList.Builder<ImmutableList<ItemStack>> pennants = ImmutableList.builder();
            for (int i = 0; i < pattern.size(); i++) {
                pennants.add(ImmutableList.of(pattern.get(i)));
            }
            return pennants.build();
        }

        @Override
        public boolean dictatesOutputType() {
            return true;
        }

        @Override
        public List<ItemStack> accumulator() {
            return new ArrayList<>();
        }

        @Override
        public void consume(final List<ItemStack> patternList, final ItemStack ingredient) {
            patternList.add(ingredient);
        }

        @Override
        public boolean finish(final List<ItemStack> pattern, final ModifiableDataComponentMap nbt) {
            if (!pattern.isEmpty()) {
                nbt.set(FLComponents.PATTERN, pattern);
                nbt.set(FLComponents.STYLED_STRING, new StyledString());
            }
            return false;
        }

        @Override
        public void addTooltip(final List<Component> tooltip) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.pennantBunting.pennant"));
        }
    }
}
