package party.lemons.sleeprework.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class TirednessModifierEffect extends MobEffect {
    private final float modifier;

    public TirednessModifierEffect(float modifier, MobEffectCategory category, int color) {
        super(category, color);

        this.modifier = modifier;
    }

    public float getModifier() {
        return modifier;
    }
}
