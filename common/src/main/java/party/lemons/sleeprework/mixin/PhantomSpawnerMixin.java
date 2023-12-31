package party.lemons.sleeprework.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import party.lemons.sleeprework.SleepRework;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin
{
    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void tick(ServerLevel arg, boolean bl, boolean bl2, CallbackInfoReturnable<Integer> cbi)
    {
        if(SleepRework.CONFIG.phantomConfig().disableVanillaPhantomSpawner())
            cbi.cancel();
    }
}
