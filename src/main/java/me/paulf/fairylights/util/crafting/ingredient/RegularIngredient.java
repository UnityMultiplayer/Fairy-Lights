package me.paulf.fairylights.util.crafting.ingredient;

import me.paulf.fairylights.server.item.components.ModifiableDataComponentMap;
import me.paulf.fairylights.util.crafting.GenericRecipe;
import net.minecraft.world.item.ItemStack;

public interface RegularIngredient extends GenericIngredient<RegularIngredient, GenericRecipe.MatchResultRegular> {
    default void matched(final ItemStack ingredient, final ModifiableDataComponentMap components) {}
}
