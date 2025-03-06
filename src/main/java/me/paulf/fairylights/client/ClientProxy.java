package me.paulf.fairylights.client;

import com.google.common.collect.ImmutableList;
import dev.architectury.event.events.client.ClientGuiEvent;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;
import me.paulf.fairylights.FairyLights;
import me.paulf.fairylights.client.command.JinglerCommand;
import me.paulf.fairylights.client.model.light.*;
import me.paulf.fairylights.client.renderer.block.entity.*;
import me.paulf.fairylights.client.renderer.entity.FenceFastenerRenderer;
import me.paulf.fairylights.client.tutorial.ClippyController;
import me.paulf.fairylights.server.ServerProxy;
import me.paulf.fairylights.server.block.entity.FLBlockEntities;
import me.paulf.fairylights.server.entity.FLEntities;
import me.paulf.fairylights.server.feature.light.ColorChangingBehavior;
import me.paulf.fairylights.server.item.DyeableItem;
import me.paulf.fairylights.server.item.FLItems;
import me.paulf.fairylights.server.item.components.FLComponents;
import me.paulf.fairylights.server.net.clientbound.JingleMessage;
import me.paulf.fairylights.server.net.clientbound.OpenEditLetteredConnectionScreenMessage;
import me.paulf.fairylights.server.net.clientbound.UpdateEntityFastenerMessage;
import me.paulf.fairylights.server.string.StringTypes;
import me.paulf.fairylights.util.styledstring.StyledString;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.config.ModConfig;

public final class ClientProxy extends ServerProxy {
    @SuppressWarnings("deprecation")
    public static final Material SOLID_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "entity/connections"));

    @SuppressWarnings("deprecation")
    public static final Material TRANSLUCENT_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath(FairyLights.ID, "entity/connections"));

    private final ImmutableList<ResourceLocation> entityModels = new ImmutableList.Builder<ResourceLocation>()
        .addAll(PennantBuntingRenderer.MODELS)
        .addAll(LetterBuntingRenderer.MODELS.values())
        .build();

    @Override
    public void init() {
        ClientPlayNetworking.registerGlobalReceiver(JingleMessage.TYPE, new JingleMessage.Handler());
        ClientPlayNetworking.registerGlobalReceiver(OpenEditLetteredConnectionScreenMessage.TYPE, new OpenEditLetteredConnectionScreenMessage.Handler());
        ClientPlayNetworking.registerGlobalReceiver(UpdateEntityFastenerMessage.TYPE, new UpdateEntityFastenerMessage.Handler());

        super.init();
        new ClippyController().init();
        ForgeConfigRegistry.INSTANCE.register(FairyLights.ID, ModConfig.Type.CLIENT, FLClientConfig.SPEC);
        ClientEventHandler clientEventHandler = new ClientEventHandler();
        ClientGuiEvent.RENDER_HUD.register((guiGraphics, deltaTracker) -> {
            var mc = Minecraft.getInstance();
            clientEventHandler.renderOverlay(mc.gui, guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(true), mc.getWindow().getScreenWidth(), mc.getWindow().getScreenHeight());
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            JinglerCommand.register(dispatcher);
        });
        JinglerCommand.register();
 
        setup();
        setupLayerDefinitions();
        setupColors();
        setupModels();
    }

    /*private int getUvIndex(VertexFormat vertexFormat) {
        int position = 0;
        for (final VertexFormatElement ee : vertexFormat.getElements()) {
            if (ee.getUsage() == VertexFormatElement.Usage.UV) {
                if (position % 4 == 0) {
                    return position / 4;
                }
                break;
            }
            position += ee.getByteSize();
        }
        return -1;
    }*/

    private void recomputeUv(final int stride, final int finalUvOffset, final BakedModel model) {
        final TextureAtlasSprite sprite = model.getParticleIcon();
        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        float uSize = uMax - uMin;
        float vSize = vMax - vMin;

        final int w = (int) (uSize / (sprite.getU1() - sprite.getU0()));
        final int h = (int) (vSize / (sprite.getV1() - sprite.getV0()));
        for (final BakedQuad quad : model.getQuads(null, null, RandomSource.create(42L))) {
            final int[] data = quad.getVertices();
            for (int n = 0; n < 4; n++) {
                int iu = n * stride + finalUvOffset;
                int iv = n * stride + finalUvOffset + 1;
                data[iu] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[iu]) * w) / w);
                data[iv] = Float.floatToIntBits((float) Math.round(Float.intBitsToFloat(data[iv]) * h) / h);
            }
        }
    }

    private void setup() {
        BlockEntityRenderers.register(FLBlockEntities.FASTENER.get(), context -> new FastenerBlockEntityRenderer(context, ServerProxy.buildBlockView()));
        BlockEntityRenderers.register(FLBlockEntities.LIGHT.get(), LightBlockEntityRenderer::new);
        EntityRendererRegistry.register(FLEntities.FASTENER.get(), FenceFastenerRenderer::new);
        /*final LightRenderer r = new LightRenderer();
        final StringBuilder bob = new StringBuilder();
        FLItems.lights().forEach(l -> {
            final LightModel<?> model = r.getModel(l.getBlock().getVariant(), -1);
            final AxisAlignedBB bb = model.getBounds();
            bob.append(String.format("%n%s new AxisAlignedBB(%.3fD, %.3fD, %.3fD, %.3fD, %.3fD, %.3fD), %.3fD", l.getRegistryName(), bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, model.getFloorOffset()));
        });
        LogManager.getLogger().debug("waldo {}", bob);*/
    }

    private void setupLayerDefinitions() {
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.BOW, BowModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.GARLAND_RINGS, GarlandVineRenderer.RingsModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.TINSEL_STRIP, GarlandTinselRenderer.StripModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.FAIRY_LIGHT, FairyLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.PAPER_LANTERN, PaperLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ORB_LANTERN, OrbLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.FLOWER_LIGHT, FlowerLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.CANDLE_LANTERN_LIGHT, ColorCandleLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.OIL_LANTERN_LIGHT, ColorOilLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.JACK_O_LANTERN, JackOLanternLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.SKULL_LIGHT, SkullLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.GHOST_LIGHT, GhostLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.SPIDER_LIGHT, SpiderLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.WITCH_LIGHT, WitchLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.SNOWFLAKE_LIGHT, SnowflakeLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.HEART_LIGHT, HeartLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.MOON_LIGHT, MoonLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.STAR_LIGHT, StarLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ICICLE_LIGHTS_1, () -> IcicleLightsModel.createLayer(1));
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ICICLE_LIGHTS_2, () -> IcicleLightsModel.createLayer(2));
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ICICLE_LIGHTS_3, () -> IcicleLightsModel.createLayer(3));
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.ICICLE_LIGHTS_4, () -> IcicleLightsModel.createLayer(4));
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.METEOR_LIGHT, MeteorLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.OIL_LANTERN, OilLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.CANDLE_LANTERN, CandleLanternModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.INCANDESCENT_LIGHT, IncandescentLightModel::createLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.LETTER_WIRE, LetterBuntingRenderer::wireLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.PENNANT_WIRE, PennantBuntingRenderer::wireLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.TINSEL_WIRE, GarlandTinselRenderer::wireLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.VINE_WIRE, GarlandVineRenderer::wireLayer);
        EntityModelLayerRegistry.registerModelLayer(FLModelLayers.LIGHTS_WIRE, HangingLightsRenderer::wireLayer);
    }

    private void setupModels() {
        ModelLoadingPlugin.register(context -> {
            context.addModels(FenceFastenerRenderer.MODEL);
            context.addModels(this.entityModels);
        });
    }

    private void setupColors() {
        ColorHandlersCallback.ITEM.register((event, blockColors) -> {
            event.register((stack, index) -> {
                    if (index == 1) {
                        if (ColorChangingBehavior.exists(stack)) {
                            return ColorChangingBehavior.animate(stack);
                        }
                        return DyeableItem.getColor(stack);
                    }
                    return 0xFFFFFFFF;
                },
                FLItems.FAIRY_LIGHT.get(),
                FLItems.PAPER_LANTERN.get(),
                FLItems.ORB_LANTERN.get(),
                FLItems.FLOWER_LIGHT.get(),
                FLItems.CANDLE_LANTERN_LIGHT.get(),
                FLItems.OIL_LANTERN_LIGHT.get(),
                FLItems.JACK_O_LANTERN.get(),
                FLItems.SKULL_LIGHT.get(),
                FLItems.GHOST_LIGHT.get(),
                FLItems.SPIDER_LIGHT.get(),
                FLItems.WITCH_LIGHT.get(),
                FLItems.SNOWFLAKE_LIGHT.get(),
                FLItems.HEART_LIGHT.get(),
                FLItems.MOON_LIGHT.get(),
                FLItems.STAR_LIGHT.get(),
                FLItems.ICICLE_LIGHTS.get(),
                FLItems.METEOR_LIGHT.get()
            );
            event.register((stack, index) -> {
                if (index == 0) {
                    if (stack.has(FLComponents.STRING_TYPE)) {
                        return 0xFF000000 | stack.get(FLComponents.STRING_TYPE).getColor();
                    }
                    return 0xFF000000 | StringTypes.BLACK_STRING.get().getColor();
                }
                if (stack.has(FLComponents.PATTERN)) {
                    var pattern = stack.get(FLComponents.PATTERN);
                    if (!pattern.isEmpty()) {
                        var item = pattern.get((index - 1) % pattern.size());
                        if (ColorChangingBehavior.exists(item)) {
                            return ColorChangingBehavior.animate(item);
                        }
                        return DyeableItem.getColor(item);
                    }
                }
                if (FairyLights.CHRISTMAS.isOccurringNow()) {
                    return (index + Util.getMillis() / 2000) % 2 == 0 ? 0xFF993333 : 0xFF7FCC19;
                }
                if (FairyLights.HALLOWEEN.isOccurringNow()) {
                    return index % 2 == 0 ? 0xFFf9801d : 0xFF8932b8;
                }
                return 0xFFFFD584;
            }, FLItems.HANGING_LIGHTS.get());
            event.register((stack, index) -> index == 0 ? DyeableItem.getColor(stack) : 0xFFFFFFFF, FLItems.TINSEL.get());
            event.register((stack, index) -> {
                if (index == 0) {
                    return 0xFFFFFFFF;
                }
                if (stack.has(FLComponents.PATTERN)) {
                    var pattern = stack.get(FLComponents.PATTERN);
                    if (!pattern.isEmpty()) {
                        var light = pattern.get((index - 1) % pattern.size());
                        return DyeableItem.getColor(light);
                    }
                }
                return 0xFFFFFFFF;
            }, FLItems.PENNANT_BUNTING.get());
            event.register(ClientProxy::secondLayerColor, FLItems.TRIANGLE_PENNANT.get());
            event.register(ClientProxy::secondLayerColor, FLItems.SPEARHEAD_PENNANT.get());
            event.register(ClientProxy::secondLayerColor, FLItems.SWALLOWTAIL_PENNANT.get());
            event.register(ClientProxy::secondLayerColor, FLItems.SQUARE_PENNANT.get());
            event.register((stack, index) -> {
                if (index > 0 && stack.has(FLComponents.STYLED_STRING)) {
                    final StyledString str = stack.get(FLComponents.STYLED_STRING);
                    if (str.length() > 0) {
                        ChatFormatting lastColor = null, color = null;
                        int n = (index - 1) % str.length();
                        for (int i = 0; i < str.length(); lastColor = color, i++) {
                            color = str.styleAt(i).getColor();
                            if (lastColor != color && (n-- == 0)) {
                                break;
                            }
                        }
                        return StyledString.getColor(color) | 0xFF000000;
                    }
                }
                return 0xFFFFFFFF;
            }, FLItems.LETTER_BUNTING.get());
        });
    }

    private static int secondLayerColor(final ItemStack stack, final int index) {
        return index == 0 ? 0xFFFFFF : DyeableItem.getColor(stack);
    }
}
