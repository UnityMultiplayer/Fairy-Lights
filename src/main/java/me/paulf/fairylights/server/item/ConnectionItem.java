package me.paulf.fairylights.server.item;

import dev.architectury.registry.registries.RegistrySupplier;
import me.paulf.fairylights.server.block.FLBlocks;
import me.paulf.fairylights.server.block.FastenerBlock;
import me.paulf.fairylights.server.capability.CapabilityHandler;
import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.connection.ConnectionType;
import me.paulf.fairylights.server.entity.FenceFastenerEntity;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.item.components.FLComponents;
import me.paulf.fairylights.server.sound.FLSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public abstract class ConnectionItem extends Item {
    private final RegistrySupplier<? extends ConnectionType<?>> type;

    public ConnectionItem(final Properties properties, final RegistrySupplier<? extends ConnectionType<?>> type) {
        super(properties);
        this.type = type;
    }

    public final ConnectionType<?> getConnectionType() {
        return (ConnectionType<?>) this.type.get();
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final Player user = context.getPlayer();
        if (user == null) {
            return super.useOn(context);
        }
        final Level world = context.getLevel();
        final Direction side = context.getClickedFace();
        final BlockPos clickPos = context.getClickedPos();
        final Block fastener = FLBlocks.FASTENER.get();
        final ItemStack stack = context.getItemInHand();
        if (this.isConnectionInOtherHand(world, user, stack)) {
            return InteractionResult.PASS;
        }
        final BlockState fastenerState = fastener.defaultBlockState().setValue(FastenerBlock.FACING, side);
        final BlockState currentBlockState = world.getBlockState(clickPos);
        final BlockPlaceContext blockContext = new BlockPlaceContext(context);
        final BlockPos placePos = blockContext.getClickedPos();
        if (currentBlockState.getBlock() == fastener) {
            if (!world.isClientSide()) {
                this.connect(stack, user, world, clickPos);
            }
            return InteractionResult.SUCCESS;
        } else if (blockContext.canPlace() && fastenerState.canSurvive(world, placePos)) {
            if (!world.isClientSide()) {
                this.connect(stack, user, world, placePos, fastenerState);
            }
            return InteractionResult.SUCCESS;
        } else if (isFence(currentBlockState)) {
            final BlockAttachedEntity entity = FenceFastenerEntity.findHanging(world, clickPos);
            if (entity == null || entity instanceof FenceFastenerEntity) {
                if (!world.isClientSide()) {
                    this.connectFence(stack, user, world, clickPos, (FenceFastenerEntity) entity);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private boolean isConnectionInOtherHand(final Level world, final Player user, final ItemStack stack) {
        final Fastener<?> attacher = CapabilityHandler.FASTENER_CAP.maybeGet(user).orElseThrow(IllegalStateException::new);
        return attacher.getFirstConnection().filter(connection -> {
            final CompoundTag nbt = connection.serializeLogic(world.registryAccess());

            if (nbt.isEmpty())
                return true;

            if (stack.has(FLComponents.CONNECTION_DATA))
                return !NbtUtils.compareNbt(nbt, stack.get(FLComponents.CONNECTION_DATA), true);

            return false;
        }).isPresent();
    }

    private void connect(final ItemStack stack, final Player user, final Level world, final BlockPos pos) {
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity != null) {
            CapabilityHandler.FASTENER_CAP.maybeGet(entity).ifPresent(fastener -> this.connect(stack, user, world, fastener));
        }
    }

    private void connect(final ItemStack stack, final Player user, final Level world, final BlockPos pos, final BlockState state) {
        if (world.setBlock(pos, state, 3)) {
            state.getBlock().setPlacedBy(world, pos, state, user, stack);
            final SoundType sound = state.getSoundType();
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                sound.getPlaceSound(),
                SoundSource.BLOCKS,
                (sound.getVolume() + 1) / 2,
                sound.getPitch() * 0.8F
            );
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity != null) {
                CapabilityHandler.FASTENER_CAP.maybeGet(entity).ifPresent(destination -> this.connect(stack, user, world, destination, false));
            }
        }
    }

    public void connect(final ItemStack stack, final Player user, final Level world, final Fastener<?> fastener) {
        this.connect(stack, user, world, fastener, true);
    }

    public void connect(final ItemStack stack, final Player user, final Level world, final Fastener<?> fastener, final boolean playConnectSound) {
        CapabilityHandler.FASTENER_CAP.maybeGet(user).ifPresent(attacher -> {
            boolean playSound = playConnectSound;
            final Optional<Connection> placing = attacher.getFirstConnection();
            if (placing.isPresent()) {
                final Connection conn = placing.get();
                if (conn.reconnect(fastener)) {
                    conn.onConnect(world, user, stack);
                    stack.shrink(1);
                } else {
                    playSound = false;
                }
            } else {
                fastener.connect(world, attacher, this.getConnectionType(), stack.getComponents(), false);
            }
            if (playSound) {
                final Vec3 pos = fastener.getConnectionPoint();
                world.playSound(null, pos.x, pos.y, pos.z, FLSounds.CORD_CONNECT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        });
    }

    private void connectFence(final ItemStack stack, final Player user, final Level world, final BlockPos pos, FenceFastenerEntity fastener) {
        final boolean playConnectSound;
        if (fastener == null) {
            fastener = FenceFastenerEntity.create(world, pos);
            playConnectSound = false;
        } else {
            playConnectSound = true;
        }
        this.connect(stack, user, world, CapabilityHandler.FASTENER_CAP.maybeGet(fastener).orElseThrow(IllegalStateException::new), playConnectSound);
    }

    public static boolean isFence(final BlockState state) {
        return state.isSolid() && state.is(BlockTags.FENCES);
    }
}
