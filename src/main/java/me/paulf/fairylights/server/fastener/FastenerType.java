package me.paulf.fairylights.server.fastener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.paulf.fairylights.server.fastener.accessor.BlockFastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.FenceFastenerAccessor;
import me.paulf.fairylights.server.fastener.accessor.PlayerFastenerAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public enum FastenerType {
    BLOCK(BlockFastenerAccessor::new),
    FENCE(FenceFastenerAccessor::new),
    PLAYER(PlayerFastenerAccessor::new);

    private static final Map<String, FastenerType> NAME_TO_TYPE = new HashMap<>();

    static {
        for (final FastenerType type : values()) {
            NAME_TO_TYPE.put(type.name, type);
        }
    }

    public static final Codec<FastenerAccessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type")
                    .forGetter(accessor -> accessor.getType().name),
            CompoundTag.CODEC.fieldOf("data")
                    .forGetter(FastenerAccessor::serialize)
        )
            .apply(instance, (type, data) -> {
                var accessor = NAME_TO_TYPE.get(type).createAccessor();
                accessor.deserialize(data);
                return accessor;
            })
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FastenerAccessor> STREAM_CODEC = StreamCodec.of(
            (buf, accessor) -> buf.writeNbt(serialize(accessor)),
            buf -> deserialize(Objects.requireNonNull(buf.readNbt()))
    );

    private final Supplier<? extends FastenerAccessor> supplier;

    private final String name;

    FastenerType(final Supplier<? extends FastenerAccessor> supplier) {
        this.supplier = supplier;
        this.name = this.name().toLowerCase(Locale.ENGLISH);
    }

    public final FastenerAccessor createAccessor() {
        return this.supplier.get();
    }

    public static CompoundTag serialize(final FastenerAccessor accessor) {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, accessor).getOrThrow();
    }

    public static FastenerAccessor deserialize(final CompoundTag compound) {
        return CODEC.decode(NbtOps.INSTANCE, compound).getOrThrow().getFirst();
    }
}
