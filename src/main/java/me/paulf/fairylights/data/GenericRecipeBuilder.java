package me.paulf.fairylights.data;

import io.netty.buffer.Unpooled;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.mixin.fabric.AdvancementBuilderAccessor;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Objects;
import java.util.Optional;

public class GenericRecipeBuilder {
    private final RecipeSerializer<?> serializer;

    private final Advancement.Builder advancementBuilder = Advancement.Builder.advancement();

    public GenericRecipeBuilder(final RecipeSerializer<?> serializer) {
        this.serializer = Objects.requireNonNull(serializer, "serializer");
    }

    public GenericRecipeBuilder unlockedBy(final String name, final Criterion<?> criterion) {
        this.advancementBuilder.addCriterion(name, criterion);
        return this;
    }

    public void build(final RecipeOutput consumer, final ResourceLocation id) {
        final AdvancementHolder advancementHolder;
        final ResourceLocation advancementId;
        if (((AdvancementBuilderAccessor) this.advancementBuilder).getCriteria().build().isEmpty()) {
            advancementHolder = null;
        } else {
            advancementId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + FairyLights.ID + "/" + id.getPath());
            advancementHolder = consumer.advancement()
                .parent(ResourceLocation.withDefaultNamespace("recipes/root"))
                .addCriterion("has_the_recipe", CriteriaTriggers.RECIPE_UNLOCKED.createCriterion(new RecipeUnlockedTrigger.TriggerInstance(Optional.empty(), id)))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR)
                .build(advancementId);
        }

        // this is a dumb workaround but it works idc
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), null);
        buf.writeEnum(CraftingBookCategory.MISC);
        buf.resetReaderIndex();

        consumer.accept(id, this.serializer.streamCodec().decode(buf), advancementHolder);
    }

    public static GenericRecipeBuilder customRecipe(final RecipeSerializer<?> serializer) {
        return new GenericRecipeBuilder(serializer);
    }
}
