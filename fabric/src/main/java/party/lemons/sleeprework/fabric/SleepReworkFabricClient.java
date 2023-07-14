package party.lemons.sleeprework.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.client.SleepReworkClient;
import party.lemons.sleeprework.handler.ServerHandler;

public class SleepReworkFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SleepReworkClient.init();
    }
}
