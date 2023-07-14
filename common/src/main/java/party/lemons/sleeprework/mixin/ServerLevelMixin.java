package party.lemons.sleeprework.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import party.lemons.sleeprework.handler.ServerHandler;

import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Shadow @Final
    List<ServerPlayer> players;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;wakeUpAllPlayers()V"))
    public void tick(BooleanSupplier booleanSupplier, CallbackInfo cbi)
    {
        this.players.stream().filter(LivingEntity::isSleeping).toList().forEach(ServerHandler::handleWakeUp);
    }
}
