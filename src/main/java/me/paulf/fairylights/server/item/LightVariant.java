package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.feature.light.LightBehavior;
import me.paulf.fairylights.server.item.components.FLComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

public interface LightVariant<T extends LightBehavior> {
    boolean parallelsCord();

    float getSpacing();

    AABB getBounds();

    double getFloorOffset();

    T createBehavior(final ItemStack stack);

    boolean isOrientable();

    static Optional<LightVariant<?>> get(ItemStack stack) {
        if (!stack.has(FLComponents.LIGHT_BLOCK))
            return Optional.empty();

        var block = stack.get(FLComponents.LIGHT_BLOCK);

        if (block instanceof LightBlock light) {
            return Optional.of(light.getVariant());
        }

        return Optional.empty();
    }
}
