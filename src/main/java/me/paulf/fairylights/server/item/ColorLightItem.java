package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.item.components.FLComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ColorLightItem extends LightItem {
    public ColorLightItem(final LightBlock light, final Item.Properties properties) {
        super(light, properties);
    }

    @Override
    public Component getName(final ItemStack stack) {
        if (stack.has(FLComponents.COLORS)) {
            return Component.translatable("format.fairylights.color_changing", super.getName(stack));
        }
        return DyeableItem.getDisplayName(stack, super.getName(stack));
    }
}
