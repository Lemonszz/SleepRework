package party.lemons.sleeprework.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.client.SleepReworkClient;
import party.lemons.sleeprework.handler.ServerHandler;

@Mod(SleepRework.MODID)
public class SleepReworkForge {
    public SleepReworkForge() {
        EventBuses.registerModEventBus(SleepRework.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        SleepRework.init();

        MinecraftForge.EVENT_BUS.addListener(SleepReworkForge::onSleepEvent);

        EnvExecutor.runInEnv(Dist.CLIENT, ()-> SleepReworkClient::init);
    }

    public static void onSleepEvent(PlayerSleepInBedEvent event)
    {
        if(!event.getEntity().level().isClientSide() && !ServerHandler.canPlayerSleep(((ServerPlayer) event.getEntity())))
        {
            event.getEntity().displayClientMessage(Component.translatable("sleeprework.sleep.not_tired"), true);
            event.setResult(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
        }
    }
}
