package me.paulf.fairylights.server.connection;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.item.FLItems;
import net.minecraft.resources.ResourceKey;

public final class ConnectionTypes {
    private ConnectionTypes() {}

    public static final DeferredRegister<ConnectionType<?>> REG = DeferredRegister.create(FairyLights.ID, ResourceKey.createRegistryKey(FairyLights.CONNECTION_TYPE));

    public static final RegistrySupplier<ConnectionType<HangingLightsConnection>> HANGING_LIGHTS = REG.register("hanging_lights",
        () -> ConnectionType.Builder.create(HangingLightsConnection::new).item(FLItems.HANGING_LIGHTS).build()
    );

    public static final RegistrySupplier<ConnectionType<GarlandVineConnection>> VINE_GARLAND = REG.register("vine_garland",
        () -> ConnectionType.Builder.create(GarlandVineConnection::new).item(FLItems.GARLAND).build()
    );

    public static final RegistrySupplier<ConnectionType<GarlandTinselConnection>> TINSEL_GARLAND = REG.register("tinsel_garland",
        () -> ConnectionType.Builder.create(GarlandTinselConnection::new).item(FLItems.TINSEL).build()
    );

    public static final RegistrySupplier<ConnectionType<PennantBuntingConnection>> PENNANT_BUNTING = REG.register("pennant_bunting",
        () -> ConnectionType.Builder.create(PennantBuntingConnection::new).item(FLItems.PENNANT_BUNTING).build()
    );

    public static final RegistrySupplier<ConnectionType<LetterBuntingConnection>> LETTER_BUNTING = REG.register("letter_bunting",
        () -> ConnectionType.Builder.create(LetterBuntingConnection::new).item(FLItems.LETTER_BUNTING).build()
    );
}
