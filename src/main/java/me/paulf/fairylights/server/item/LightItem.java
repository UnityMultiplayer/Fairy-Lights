package me.paulf.fairylights.server.item;

import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.item.components.FLComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class LightItem extends BlockItem {
    private final LightBlock light;

    public LightItem(final LightBlock light, final Properties properties) {
        super(light, properties
            .component(FLComponents.LIGHT_BLOCK, light)
        );
        this.light = light;
    }

    @Override
    public LightBlock getBlock() {
        return this.light;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (stack.has(FLComponents.TWINKLES) && stack.get(FLComponents.TWINKLES)) {
            tooltip.add(Component.translatable("item.fairyLights.twinkle").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        if (stack.has(FLComponents.COLORS)) {
            var colors = stack.get(FLComponents.COLORS);
            for (Integer color : colors) {
                tooltip.add(DyeableItem.getColorName(color).copy().withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
