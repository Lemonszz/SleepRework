package party.lemons.sleeprework.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.handler.ServerHandler;

public class SleepReworkFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SleepRework.init();

        EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) -> {
            if(!player.level().isClientSide() && !ServerHandler.canPlayerSleep((ServerPlayer) player)) {
                player.displayClientMessage(Component.translatable("sleeprework.sleep.not_tired"), true);
                return Player.BedSleepingProblem.NOT_POSSIBLE_HERE;
            }
            return null;
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
            if(!entity.level().isClientSide() &&  entity instanceof ServerPlayer player)
            {
                ServerHandler.resetTimeout(player);
            }
        });
    }
}
