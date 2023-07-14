package party.lemons.sleeprework.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import party.lemons.sleeprework.SleepRework;

public class PlayerSleepData
{
    protected float tiredness = 0.0F;

    public boolean canSleep()
    {
        return tiredness >= SleepRework.CONFIG.playerConfig.minSleepLevel;
    }

    public boolean doesSpawnPhantoms()
    {
        return SleepRework.CONFIG.phantomConfig.tirednessPhantoms && tiredness >= SleepRework.CONFIG.phantomConfig.phantomSpawnTiredness;
    }

    public void save(CompoundTag tag)
    {
        tag.putFloat(TAG_TIREDNESS, tiredness);
    }

    public void load(CompoundTag tag)
    {
        tiredness = tag.getFloat(TAG_TIREDNESS);
    }

    protected static final String TAG_TIREDNESS = "SR_Tiredness";

    public void setTiredness(@Nullable Player serverPlayer, float value)
    {
        tiredness = value;
    }

    public float getTiredness() {
        return tiredness;
    }
}
