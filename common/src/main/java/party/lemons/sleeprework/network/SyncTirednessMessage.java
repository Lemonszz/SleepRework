package party.lemons.sleeprework.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;
import party.lemons.sleeprework.SleepRework;

public class SyncTirednessMessage extends BaseS2CMessage {

    private final float tiredness;

    public SyncTirednessMessage(FriendlyByteBuf buf)
    {
        this(buf.readFloat());
    }

    public SyncTirednessMessage(float tiredness)
    {
        this.tiredness = tiredness;
    }

    @Override
    public MessageType getType() {
        return SleepReworkNetwork.SYNC_TIREDNESS;
    }

    @Override
    public void write(FriendlyByteBuf buf)
    {
        buf.writeFloat(tiredness);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(()->{
            SleepRework.localSleepData.setTiredness(null, tiredness);
        });
    }
}
