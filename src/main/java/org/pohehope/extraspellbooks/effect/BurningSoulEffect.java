package org.pohehope.extraspellbooks.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BurningSoulEffect extends MagicMobEffect {
    public static final float ATTACK_DAMAGE_LEVEL = 1.5f;
    public static final float SPEED_LEVEL = .2f;

    @Override
    public void applyEffectTick(LivingEntity entity,int amplifier) {
        entity.setRemainingFireTicks(60);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public BurningSoulEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
}
