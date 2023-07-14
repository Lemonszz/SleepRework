package party.lemons.sleeprework.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import party.lemons.sleeprework.handler.ServerPlayerSleepData;
import party.lemons.sleeprework.handler.SleepDataHolder;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements SleepDataHolder {

    @Unique
    private final ServerPlayerSleepData sleepData = new ServerPlayerSleepData();

    @Override
    public ServerPlayerSleepData getSleepData() {
        return sleepData;
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void addAdditionalSaveData(CompoundTag tag, CallbackInfo cbi)
    {
        CompoundTag sleepTag = new CompoundTag();
        sleepData.save(sleepTag);
        tag.put(TAG_SLEEP_DATA, sleepTag);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void readAdditionalSaveData(CompoundTag tag, CallbackInfo cbi)
    {
        if(tag.contains(TAG_SLEEP_DATA))
        {
            CompoundTag sleepTag = tag.getCompound(TAG_SLEEP_DATA);
            sleepData.load(sleepTag);
            sleepData.syncTo(((ServerPlayer)(Object)this));
        }
    }

    /*
    @Inject(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z"), cancellable = true)
    private void startSleepInBed(BlockPos blockPos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cbi)
    {
        if(!ServerHandler.canPlayerSleep(((ServerPlayer)(Object)this)))
        {
            ((ServerPlayer)(Object)this).displayClientMessage(Component.translatable("sleeprework.sleep.not_tired"), true);
            cbi.setReturnValue(Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE));
        }
    }*/

    private static final String TAG_SLEEP_DATA = "SR_SleepData";
}
