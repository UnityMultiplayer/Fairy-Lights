package me.paulf.fairylights.mixin.fabric;

import me.paulf.fairylights.server.ServerProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {
    @Shadow @Final public static EnumProperty<NoteBlockInstrument> INSTRUMENT;

    @Inject(method = "triggerEvent", at = @At("HEAD"), cancellable = true)
    private void runFastenerNoteBlockEvent(BlockState state, Level level, BlockPos pos, int id, int param, CallbackInfoReturnable<Boolean> cir) {
        if (ServerProxy.serverHandler.onNoteBlockPlay(level, pos, id, state.getValue(INSTRUMENT)))
            cir.setReturnValue(false);
    }
}
