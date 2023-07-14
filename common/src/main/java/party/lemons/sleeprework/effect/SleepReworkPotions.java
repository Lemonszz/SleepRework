package party.lemons.sleeprework.effect;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import party.lemons.sleeprework.SleepRework;
import party.lemons.sleeprework.mixin.PotionBrewingInvoker;

import java.util.function.Supplier;

public class SleepReworkPotions
{
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(SleepRework.MODID, Registries.MOB_EFFECT);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(SleepRework.MODID, Registries.POTION);
    public static final RegistrySupplier<MobEffect> LIVELINESS = registerEffect("liveliness", ()->new TirednessModifierEffect(SleepRework.CONFIG.potionConfig.livelinessTirednessModifier, MobEffectCategory.BENEFICIAL, 0xddff00));
    public static final RegistrySupplier<MobEffect> DROWSINESS = registerEffect("drowsiness", ()->new TirednessModifierEffect(SleepRework.CONFIG.potionConfig.drowsinessTirednessModifier, MobEffectCategory.HARMFUL, 0x3c2d5c));

    public static final RegistrySupplier<Potion> LIVELINESS_POTION = registerPotion("liveliness", ()->new Potion(new MobEffectInstance(LIVELINESS.get(), 24000)));
    public static final RegistrySupplier<Potion> LONG_LIVELINESS_POTION = registerPotion("long_liveliness", ()->new Potion(new MobEffectInstance(LIVELINESS.get(), 36000)));
    public static final RegistrySupplier<Potion> STRONG_LIVELINESS_POTION = registerPotion("strong_liveliness", ()->new Potion(new MobEffectInstance(LIVELINESS.get(), 15000, 1)));
    public static final RegistrySupplier<Potion> DROWSINESS_POTION = registerPotion("drowsiness", ()->new Potion(new MobEffectInstance(DROWSINESS.get(), 24000)));
    public static final RegistrySupplier<Potion> LONG_DROWSINESS_POTION = registerPotion("long_drowsiness", ()->new Potion(new MobEffectInstance(DROWSINESS.get(), 36000)));
    public static final RegistrySupplier<Potion> STRONG_DROWSINESS_POTION = registerPotion("strong_drowsiness", ()->new Potion(new MobEffectInstance(DROWSINESS.get(), 15000, 1)));

    public static void init()
    {
        MOB_EFFECTS.register();
        POTIONS.register();


        LifecycleEvent.SETUP.register(()->{
            PotionBrewingInvoker.callAddMix(Potions.AWKWARD, Items.HONEYCOMB, LIVELINESS_POTION.get());
            PotionBrewingInvoker.callAddMix(LIVELINESS_POTION.get(), Items.REDSTONE, LONG_LIVELINESS_POTION.get());
            PotionBrewingInvoker.callAddMix(LIVELINESS_POTION.get(), Items.GLOWSTONE, STRONG_LIVELINESS_POTION.get());

            PotionBrewingInvoker.callAddMix(LIVELINESS_POTION.get(), Items.FERMENTED_SPIDER_EYE, DROWSINESS_POTION.get());
            PotionBrewingInvoker.callAddMix(LONG_LIVELINESS_POTION.get(), Items.FERMENTED_SPIDER_EYE, LONG_DROWSINESS_POTION.get());
            PotionBrewingInvoker.callAddMix(STRONG_LIVELINESS_POTION.get(), Items.FERMENTED_SPIDER_EYE, STRONG_DROWSINESS_POTION.get());

            PotionBrewingInvoker.callAddMix(DROWSINESS_POTION.get(), Items.REDSTONE, LONG_DROWSINESS_POTION.get());
            PotionBrewingInvoker.callAddMix(DROWSINESS_POTION.get(), Items.GLOWSTONE, STRONG_DROWSINESS_POTION.get());
        });
    }

    private static RegistrySupplier<MobEffect> registerEffect(String name, Supplier<MobEffect> mobEffect)
    {
        return MOB_EFFECTS.register(SleepRework.id(name), mobEffect);
    }

    private static RegistrySupplier<Potion> registerPotion(String name, Supplier<Potion> potion)
    {
        return POTIONS.register(SleepRework.id(name), potion);
    }
}
