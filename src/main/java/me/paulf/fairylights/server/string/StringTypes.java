package me.paulf.fairylights.server.string;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.paulf.fairylights.FairyLights;
import net.minecraft.resources.ResourceKey;

public final class StringTypes {
    private StringTypes() {}

    public static final DeferredRegister<StringType> REG = DeferredRegister.create(FairyLights.ID, ResourceKey.createRegistryKey(FairyLights.STRING_TYPE));

    public static final RegistrySupplier<StringType> BLACK_STRING = REG.register("black_string", () -> new StringType(0x323232));

    public static final RegistrySupplier<StringType> WHITE_STRING = REG.register("white_string", () -> new StringType(0xF0F0F0));
}
