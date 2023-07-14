package party.lemons.sleeprework.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.effect.SleepReworkPotions;
import party.lemons.sleeprework.effect.TirednessModifierEffect;
import party.lemons.sleeprework.network.SyncTirednessMessage;

public class ServerPlayerSleepData extends PlayerSleepData
{
    protected float sleepTimeout = 0.0F;

    public void increaseTiredness(ServerPlayer player)
    {
        float increase = getTirednessIncrease(player);

        setTiredness(player, tiredness + increase);
    }

    public float getTirednessIncrease(ServerPlayer player)
    {
        float increase = SleepRework.CONFIG.playerConfig.tirednessIncreasePerMinute;
        if(player.hasEffect(SleepReworkPotions.DROWSINESS.get()))
        {
            MobEffectInstance instance = player.getEffect(SleepReworkPotions.DROWSINESS.get());
            increase *= (((TirednessModifierEffect)instance.getEffect()).getModifier() * (1F + instance.getAmplifier()));
        }

        if(player.hasEffect(SleepReworkPotions.LIVELINESS.get()))
        {
            MobEffectInstance instance = player.getEffect(SleepReworkPotions.LIVELINESS.get());
            float modifier = ((TirednessModifierEffect)instance.getEffect()).getModifier();
            float amplifier = 1F + instance.getAmplifier();

            increase *= Math.pow(modifier, amplifier);
        }

        return increase;
    }

    public void resetTiredness(ServerPlayer serverPlayer)
    {
        setTiredness(serverPlayer, 0);
    }

    @Override
    public void setTiredness(@Nullable Player player, float value)
    {
        tiredness = Mth.clamp(value, 0.0F, SleepRework.CONFIG.playerConfig.maxTiredness);

        if(player != null)
            syncTo((ServerPlayer) player);
    }

    public void syncTo(ServerPlayer player)
    {
        if(player.connection != null)
            new SyncTirednessMessage(tiredness).sendTo(player);
    }
}
