package me.paulf.fairylights.server.integration.jei;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.stream.Collectors;

@JeiPlugin
public final class FairyLightsJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "plugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(final IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(GenericRecipe.class, new GenericRecipeWrapper());
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {
        final ClientLevel world = Minecraft.getInstance().level;
        final RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(
            RecipeTypes.CRAFTING,
            recipeManager.getRecipes().stream()
                .filter(holder -> GenericRecipe.class.isInstance(holder.value()))
                .filter(holder -> holder.value().isSpecial())
                .map(holder -> (RecipeHolder<CraftingRecipe>) holder)
                .collect(Collectors.toList())
        );
    }

    @Override
    public void registerItemSubtypes(final ISubtypeRegistration registry) {
        registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, FLItems.TINSEL.get(), new ColorSubtypeInterpreter());
        registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, FLItems.TRIANGLE_PENNANT.get(), new ColorSubtypeInterpreter());
        registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, FLItems.SPEARHEAD_PENNANT.get(), new ColorSubtypeInterpreter());
        registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, FLItems.SWALLOWTAIL_PENNANT.get(), new ColorSubtypeInterpreter());
        registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, FLItems.SQUARE_PENNANT.get(), new ColorSubtypeInterpreter());
        FLItems.lights().forEach(i -> registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, i, new ColorSubtypeInterpreter()));
    }
}
