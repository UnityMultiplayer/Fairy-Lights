package me.paulf.fairylights.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
    CompoundTag serialize(HolderLookup.Provider registries);

    void deserialize(CompoundTag compound, HolderLookup.Provider registries);
}
