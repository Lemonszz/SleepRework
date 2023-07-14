package party.lemons.sleeprework.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import party.lemons.sleeprework.SleepRework;

public class SleepReworkNetwork {
    public static final SimpleNetworkManager NET = SimpleNetworkManager.create(SleepRework.MODID);
    public static final MessageType SYNC_TIREDNESS = NET.registerS2C("sync_tiredness", SyncTirednessMessage::new);

    public static void init()
    {
        //nofu
    }
}
