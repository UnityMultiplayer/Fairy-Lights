package me.paulf.fairylights.server.net;

import me.paulf.fairylights.server.fastener.FastenerType;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record ConnectionInfo(
        BlockPos pos, FastenerAccessor accessor, UUID uuid
) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ConnectionInfo> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConnectionInfo::pos,
            FastenerType.STREAM_CODEC, ConnectionInfo::accessor,
            UUIDUtil.STREAM_CODEC, ConnectionInfo::uuid,
            ConnectionInfo::new
    );
}
