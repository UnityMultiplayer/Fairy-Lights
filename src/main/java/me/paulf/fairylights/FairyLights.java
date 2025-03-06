package me.paulf.fairylights;

import me.paulf.fairylights.client.ClientProxy;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.entity.FLBlockEntities;
import me.paulf.fairylights.server.connection.ConnectionType;
import me.paulf.fairylights.server.connection.ConnectionTypes;
import me.paulf.fairylights.server.creativetabs.FairyLightsItemGroup;
import me.paulf.fairylights.server.entity.FLEntities;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.components.FLComponents;
import me.paulf.fairylights.server.item.crafting.FLCraftingRecipes;
import me.paulf.fairylights.server.net.clientbound.JingleMessage;
import me.paulf.fairylights.server.net.clientbound.OpenEditLetteredConnectionScreenMessage;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import me.paulf.fairylights.server.net.serverbound.EditLetteredConnectionMessage;
import me.paulf.fairylights.server.net.serverbound.InteractionConnectionMessage;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.server.string.StringType;
import me.paulf.fairylights.server.string.StringTypes;
import me.paulf.fairylights.util.CalendarEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.time.Month;

public final class FairyLights implements ModInitializer {
    public static final String ID = "fairylights";

    public static final ResourceLocation STRING_TYPE = ResourceLocation.fromNamespaceAndPath(ID, "string_type");

    public static final ResourceLocation CONNECTION_TYPE = ResourceLocation.fromNamespaceAndPath(ID, "connection_type");

    public static final CalendarEvent CHRISTMAS = new CalendarEvent(Month.DECEMBER, 24, 26);

    public static final CalendarEvent HALLOWEEN = new CalendarEvent(Month.OCTOBER, 31, 31);

    public static Registry<ConnectionType<?>> CONNECTION_TYPES = FabricRegistryBuilder.<ConnectionType<?>>createSimple(ResourceKey.createRegistryKey(CONNECTION_TYPE))
            .attribute(RegistryAttribute.MODDED)
            .buildAndRegister();

    public static Registry<StringType> STRING_TYPES = FabricRegistryBuilder.<StringType>createDefaulted(ResourceKey.createRegistryKey(STRING_TYPE), ResourceLocation.fromNamespaceAndPath(ID, "black_string"))
            .attribute(RegistryAttribute.MODDED)
            .buildAndRegister();

    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(InteractionConnectionMessage.TYPE, InteractionConnectionMessage.CODEC);
        PayloadTypeRegistry.playC2S().register(EditLetteredConnectionMessage.TYPE, EditLetteredConnectionMessage.CODEC);
        PayloadTypeRegistry.playS2C().register(JingleMessage.TYPE, JingleMessage.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateEntityFastenerMessage.TYPE, UpdateEntityFastenerMessage.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenEditLetteredConnectionScreenMessage.TYPE, OpenEditLetteredConnectionScreenMessage.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(InteractionConnectionMessage.TYPE, new InteractionConnectionMessage.Handler());
        ServerPlayNetworking.registerGlobalReceiver(EditLetteredConnectionMessage.TYPE, new EditLetteredConnectionMessage.Handler());

        FLComponents.init();
        FLSounds.REG.register();
        FLBlocks.REG.register();
        FLEntities.REG.register();
        FLItems.REG.register();
        FLBlockEntities.REG.register();
        FLCraftingRecipes.REG.register();
        ConnectionTypes.REG.register();
        StringTypes.REG.register();

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            (new ClientProxy()).init();
        } else {
            (new ServerProxy()).init();
        }
        FairyLightsItemGroup.TAB_REG.register();
    }


}
