package me.paulf.fairylights.server.block.entity;

import io.github.fabricators_of_create.porting_lib.block.CustomUpdateTagHandlingBlockEntity;
import me.paulf.fairylights.server.block.LightBlock;
import me.paulf.fairylights.server.feature.light.Light;
import me.paulf.fairylights.server.item.LightVariant;
import me.paulf.fairylights.server.item.SimpleLightVariant;
import me.paulf.fairylights.server.sound.FLSounds;
import me.paulf.fairylights.util.FLMth;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class LightBlockEntity extends BlockEntity implements CustomUpdateTagHandlingBlockEntity {
    private Light<?> light;

    private boolean on = true;

    public LightBlockEntity(BlockPos pos, BlockState state) {
        super(FLBlockEntities.LIGHT.get(), pos, state);
        this.light = new Light<>(0, Vec3.ZERO, 0.0F, 0.0F, ItemStack.EMPTY, SimpleLightVariant.FAIRY_LIGHT, 0.0F);
    }

    public Light<?> getLight() {
        return this.light;
    }

    public void setItemStack(final ItemStack stack) {
        this.light = new Light<>(0, Vec3.ZERO, 0.0F, 0.0F, stack, LightVariant.get(stack).orElse(SimpleLightVariant.FAIRY_LIGHT), 0.0F);
        this.setComponents(stack.getComponents());
        this.setChanged();
    }

    private void setOn(final boolean on) {
        this.on = on;
        this.light.power(on, true);
        this.setChanged();
    }

    public void interact(final Level world, final BlockPos pos, final BlockState state, final Player player, final BlockHitResult hit) {
        this.setOn(!this.on);
        world.setBlockAndUpdate(pos, state.setValue(LightBlock.LIT, this.on));
        final SoundEvent lightSnd;
        final float pitch;
        if (this.on) {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNON.get();
            pitch = 0.6F;
        } else {
            lightSnd = FLSounds.FEATURE_LIGHT_TURNOFF.get();
            pitch = 0.5F;
        }
        this.level.playSound(null, pos, lightSnd, SoundSource.BLOCKS, 1.0F, pitch);
    }

    public void animateTick() {
        final BlockState state = this.getBlockState();
        final AttachFace face = state.getValue(LightBlock.FACE);
        final float rotation = state.getValue(LightBlock.FACING).toYRot();
        final MatrixStack matrix = new MatrixStack();
        matrix.translate(0.5F, 0.5F, 0.5F);
        matrix.rotate((float) Math.toRadians(180.0F - rotation), 0.0F, 1.0F, 0.0F);
        if (this.light.getVariant().isOrientable()) {
            if (face == AttachFace.WALL) {
                matrix.rotate(FLMth.HALF_PI, 1.0F, 0.0F, 0.0F);
            } else if (face == AttachFace.FLOOR) {
                matrix.rotate(-FLMth.PI, 1.0F, 0.0F, 0.0F);
            }
            matrix.translate(0.0F, 0.5F, 0.0F);
        } else {
            if (face == AttachFace.CEILING) {
                matrix.translate(0.0F, 0.25F, 0.0F);
            } else if (face == AttachFace.WALL) {
                matrix.translate(0.0F, 3.0F / 16.0F, 0.125F);
            } else {
                matrix.translate(0.0F, -(float) this.light.getVariant().getBounds().minY - 0.5F, 0.0F);
            }
        }
        this.light.getBehavior().animateTick(this.level, Vec3.atLowerCornerOf(this.worldPosition).add(matrix.transform(Vec3.ZERO)), this.light);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider providers) {
        return this.saveWithoutMetadata(providers);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider providers) {
        super.saveAdditional(compound, providers);
        compound.put("item", this.light.getItem().save(providers));
        compound.putBoolean("on", this.on);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider providers) {
        super.loadAdditional(compound, providers);
        if (compound.contains("item"))
            this.setItemStack(ItemStack.parse(providers, compound.get("item")).orElse(ItemStack.EMPTY));
        this.setOn(compound.getBoolean("on"));
    }
}
