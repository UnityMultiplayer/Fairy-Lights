package me.paulf.fairylights.server.item;

import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.server.item.components.FLComponents;
import me.paulf.fairylights.server.string.StringType;
import me.paulf.fairylights.util.RegistryObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Objects;

public final class HangingLightsConnectionItem extends ConnectionItem {
    public HangingLightsConnectionItem(final Properties properties) {
        super(properties, ConnectionTypes.HANGING_LIGHTS);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (stack.has(FLComponents.STRING_TYPE)) {
            final ResourceLocation name = RegistryObjects.getName(FairyLights.STRING_TYPES, stack.get(FLComponents.STRING_TYPE));
            tooltip.add(Component.translatable("item." + name.getNamespace() + "." + name.getPath()).withStyle(ChatFormatting.GRAY));
        }
        if (stack.has(FLComponents.PATTERN)) {
            var patternStacks = stack.get(FLComponents.PATTERN);
            if (!patternStacks.isEmpty()) {
                tooltip.add(Component.empty());
            }
            for (ItemStack lightStack : patternStacks) {
                tooltip.add(lightStack.getHoverName());
                lightStack.getItem().appendHoverText(lightStack, context, tooltip, flag);
            }
        }
    }

    public static StringType getString(final CompoundTag tag) {
        return Objects.requireNonNull(FairyLights.STRING_TYPES.get(ResourceLocation.tryParse(tag.getString("string"))));
    }

    public static void setString(final CompoundTag tag, final StringType string) {
        tag.putString("string", RegistryObjects.getName(FairyLights.STRING_TYPES, string).toString());
    }
}
