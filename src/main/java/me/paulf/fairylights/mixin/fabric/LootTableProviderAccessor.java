package me.paulf.fairylights.mixin.fabric;

import net.minecraft.data.loot.LootTableProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootTableProvider.class)
public interface LootTableProviderAccessor {
    @Accessor
    List<LootTableProvider.SubProviderEntry> getSubProviders();
}
