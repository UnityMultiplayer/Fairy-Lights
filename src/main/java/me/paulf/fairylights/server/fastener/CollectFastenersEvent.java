package me.paulf.fairylights.server.fastener;

import me.paulf.fairylights.server.capability.CapabilityHandler;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.ladysnake.cca.api.v3.component.ComponentAccess;

import java.util.ConcurrentModificationException;
import java.util.Set;

public class CollectFastenersEvent {
    public static final Event<CollectFastenersCallback> EVENT = EventFactory.createArrayBacked(CollectFastenersCallback.class, callbacks -> event -> {
        for (CollectFastenersCallback callback : callbacks) {
            callback.onCollectFasteners(event);
        }
    });

    private final Level world;

    private final AABB region;

    private final Set<Fastener<?>> fasteners;

    public CollectFastenersEvent(final Level world, final AABB region, final Set<Fastener<?>> fasteners) {
        this.world = world;
        this.region = region;
        this.fasteners = fasteners;
    }

    public Level getWorld() {
        return this.world;
    }

    public AABB getRegion() {
        return this.region;
    }

    public void accept(final LevelChunk chunk) {
        try {
            for (final BlockEntity entity : chunk.getBlockEntities().values()) {
                this.accept(entity);
            }
        } catch (final ConcurrentModificationException e) {
            // RenderChunk's may find an invalid block entity while building and trigger a remove not on main thread
        }
    }

    public void accept(final ComponentAccess provider) {
        CapabilityHandler.FASTENER_CAP.maybeGet(provider).ifPresent(this::accept);
    }

    public void accept(final Fastener<?> fastener) {
        if (this.region.contains(fastener.getConnectionPoint())) {
            this.fasteners.add(fastener);
        }
    }

    @FunctionalInterface
    public interface CollectFastenersCallback {
        void onCollectFasteners(CollectFastenersEvent event);
    }
}
