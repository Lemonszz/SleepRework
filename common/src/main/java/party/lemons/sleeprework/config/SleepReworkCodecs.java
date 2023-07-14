package party.lemons.sleeprework.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;

public class SleepReworkCodecs
{
    public static Codec<MobEffectInstance> MOB_EFFECT_INSTANCE = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(MobEffectInstance::getEffect),
                    Codec.INT.fieldOf("duration").forGetter(MobEffectInstance::getDuration),
                    Codec.INT.optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance::getAmplifier),
                    Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
                    Codec.BOOL.optionalFieldOf("visible", true).forGetter(MobEffectInstance::isVisible),
                    Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(MobEffectInstance::showIcon)
            ).apply(instance, MobEffectInstance::new)
    );
}
