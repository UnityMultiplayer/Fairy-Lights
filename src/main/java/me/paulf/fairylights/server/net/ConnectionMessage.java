package me.paulf.fairylights.server.net;

import me.paulf.fairylights.server.connection.Connection;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class ConnectionMessage implements Message {
    public BlockPos pos;
    public FastenerAccessor accessor;
    public UUID uuid;

    public ConnectionMessage(final Connection connection) {
        final Fastener<?> fastener = connection.getFastener();
        this.pos = fastener.getPos();
        this.accessor = fastener.createAccessor();
        this.uuid = connection.getUUID();
    }

    public ConnectionMessage(ConnectionInfo data) {
        this.pos = data.pos();
        this.accessor = data.accessor();
        this.uuid = data.uuid();
    }

    public ConnectionInfo getData() {
        return new ConnectionInfo(pos, accessor, uuid);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Connection> Optional<C> getConnection(final ConnectionMessage message, final Predicate<? super Connection> typePredicate, final Level world) {
        return message.accessor.get(world, false).map(Optional::of).orElse(Optional.empty()).flatMap(f -> (Optional<C>) f.get(message.uuid).filter(typePredicate));
    }
}
