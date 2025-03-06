package me.paulf.fairylights.server.connection;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

public enum PlayerAction {
    ATTACK, INTERACT;

    public static final StreamCodec<ByteBuf, PlayerAction> STREAM_CODEC = ByteBufCodecs.idMapper(ByIdMap.continuous(
            PlayerAction::ordinal, PlayerAction.values(),
            ByIdMap.OutOfBoundsStrategy.ZERO
    ), PlayerAction::ordinal);
}
