package org.pohehope.extraspellbooks.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FreezeSoulEffect extends MagicMobEffect {
    public static final float ARMOR = 3.5f;
    public static final float MOVEMENT_SPEED = -0.2f;

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.setTicksFrozen(60);
        entity.hurt(entity.damageSources().freeze(), 0f);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public FreezeSoulEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }
}
