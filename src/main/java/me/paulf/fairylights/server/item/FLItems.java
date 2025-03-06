package me.paulf.fairylights.server.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.LightBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class FLItems {
    private FLItems() {}

    public static final DeferredRegister<Item> REG = DeferredRegister.create(FairyLights.ID, Registries.ITEM);

    public static final RegistrySupplier<ConnectionItem> HANGING_LIGHTS = REG.register("hanging_lights", () -> new HangingLightsConnectionItem(defaultProperties()));

    public static final RegistrySupplier<ConnectionItem> PENNANT_BUNTING = REG.register("pennant_bunting", () -> new PennantBuntingConnectionItem(defaultProperties()));

    public static final RegistrySupplier<ConnectionItem> TINSEL = REG.register("tinsel", () -> new TinselConnectionItem(defaultProperties()));

    public static final RegistrySupplier<ConnectionItem> LETTER_BUNTING = REG.register("letter_bunting", () -> new LetterBuntingConnectionItem(defaultProperties()));

    public static final RegistrySupplier<ConnectionItem> GARLAND = REG.register("garland", () -> new GarlandConnectionItem(defaultProperties()));

    public static final RegistrySupplier<LightItem> FAIRY_LIGHT = REG.register("fairy_light", FLItems.createColorLight(FLBlocks.FAIRY_LIGHT));

    public static final RegistrySupplier<LightItem> PAPER_LANTERN = REG.register("paper_lantern", FLItems.createColorLight(FLBlocks.PAPER_LANTERN));

    public static final RegistrySupplier<LightItem> ORB_LANTERN = REG.register("orb_lantern", FLItems.createColorLight(FLBlocks.ORB_LANTERN));

    public static final RegistrySupplier<LightItem> FLOWER_LIGHT = REG.register("flower_light", FLItems.createColorLight(FLBlocks.FLOWER_LIGHT));

    public static final RegistrySupplier<LightItem> CANDLE_LANTERN_LIGHT = REG.register("candle_lantern_light", FLItems.createColorLight(FLBlocks.CANDLE_LANTERN_LIGHT));

    public static final RegistrySupplier<LightItem> OIL_LANTERN_LIGHT = REG.register("oil_lantern_light", FLItems.createColorLight(FLBlocks.OIL_LANTERN_LIGHT));

    public static final RegistrySupplier<LightItem> JACK_O_LANTERN = REG.register("jack_o_lantern", FLItems.createColorLight(FLBlocks.JACK_O_LANTERN));

    public static final RegistrySupplier<LightItem> SKULL_LIGHT = REG.register("skull_light", FLItems.createColorLight(FLBlocks.SKULL_LIGHT));

    public static final RegistrySupplier<LightItem> GHOST_LIGHT = REG.register("ghost_light", FLItems.createColorLight(FLBlocks.GHOST_LIGHT));

    public static final RegistrySupplier<LightItem> SPIDER_LIGHT = REG.register("spider_light", FLItems.createColorLight(FLBlocks.SPIDER_LIGHT));

    public static final RegistrySupplier<LightItem> WITCH_LIGHT = REG.register("witch_light", FLItems.createColorLight(FLBlocks.WITCH_LIGHT));

    public static final RegistrySupplier<LightItem> SNOWFLAKE_LIGHT = REG.register("snowflake_light", FLItems.createColorLight(FLBlocks.SNOWFLAKE_LIGHT));

    public static final RegistrySupplier<LightItem> HEART_LIGHT = REG.register("heart_light", FLItems.createColorLight(FLBlocks.HEART_LIGHT));

    public static final RegistrySupplier<LightItem> MOON_LIGHT = REG.register("moon_light", FLItems.createColorLight(FLBlocks.MOON_LIGHT));

    public static final RegistrySupplier<LightItem> STAR_LIGHT = REG.register("star_light", FLItems.createColorLight(FLBlocks.STAR_LIGHT));

    public static final RegistrySupplier<LightItem> ICICLE_LIGHTS = REG.register("icicle_lights", FLItems.createColorLight(FLBlocks.ICICLE_LIGHTS));

    public static final RegistrySupplier<LightItem> METEOR_LIGHT = REG.register("meteor_light", FLItems.createColorLight(FLBlocks.METEOR_LIGHT));

    public static final RegistrySupplier<LightItem> OIL_LANTERN = REG.register("oil_lantern", FLItems.createLight(FLBlocks.OIL_LANTERN, LightItem::new));

    public static final RegistrySupplier<LightItem> CANDLE_LANTERN = REG.register("candle_lantern", FLItems.createLight(FLBlocks.CANDLE_LANTERN, LightItem::new));

    public static final RegistrySupplier<LightItem> INCANDESCENT_LIGHT = REG.register("incandescent_light", FLItems.createLight(FLBlocks.INCANDESCENT_LIGHT, LightItem::new));

    public static final RegistrySupplier<Item> TRIANGLE_PENNANT = REG.register("triangle_pennant", () -> new PennantItem(defaultProperties()));

    public static final RegistrySupplier<Item> SPEARHEAD_PENNANT = REG.register("spearhead_pennant", () -> new PennantItem(defaultProperties()));

    public static final RegistrySupplier<Item> SWALLOWTAIL_PENNANT = REG.register("swallowtail_pennant", () -> new PennantItem(defaultProperties()));

    public static final RegistrySupplier<Item> SQUARE_PENNANT = REG.register("square_pennant", () -> new PennantItem(defaultProperties()));

    private static Item.Properties defaultProperties() {
        return new Item.Properties();
    }

    private static Supplier<LightItem> createLight(final RegistrySupplier<LightBlock> block, final BiFunction<LightBlock, Item.Properties, LightItem> factory) {
        return () -> factory.apply(block.get(), defaultProperties().stacksTo(16));
    }

    private static Supplier<LightItem> createColorLight(final RegistrySupplier<LightBlock> block) {
        return createLight(block, ColorLightItem::new);
    }

    public static Stream<LightItem> lights() {
        return StreamSupport.stream(REG.spliterator(), false)
            .flatMap(RegistrySupplier::stream)
            .filter(LightItem.class::isInstance)
            .map(LightItem.class::cast);
    }
}
