package org.pohehope.extraspellbooks.effect;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class UnderMoonEffect extends MagicMobEffect {

    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("87733c95-909c-4fc3-9780-e35a89565666");
    private static final UUID ATTACK_MODIFIER_UUID = UUID.fromString("91133c95-909c-4fc3-9780-e35a89565777");

    /*public static float getSpeed(TickEvent.LevelTickEvent level) {
        return (level.level.getDayTime() % 24000 >= 13000) ? 1.0f : 0.5f;
    }*/

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();
        float speed = (level.getDayTime() % 24000 >= 13000) ? 1.0f : 0.25f;

        // 2. 移動速度（MOVEMENT_SPEED）の変更
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            // 古いモディファイアがいったん残っていたら削除（数値を更新するため）
            speedAttribute.removeModifier(SPEED_MODIFIER_UUID);
            // 新しい数値でモディファイアを適用
            speedAttribute.addTransientModifier(new AttributeModifier(
                    SPEED_MODIFIER_UUID,
                    "UnderMoonNightMoveSpeed",
                    speed,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }

        // 3. 攻撃速度（ATTACK_SPEED）の変更
        AttributeInstance attackAttribute = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackAttribute != null) {
            attackAttribute.removeModifier(ATTACK_MODIFIER_UUID);
            attackAttribute.addTransientModifier(new AttributeModifier(
                    ATTACK_MODIFIER_UUID,
                    "UnderMoonNightAttackSpeed",
                    speed,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);

        // エフェクトが切れた、またはミルクなどで消された時に、残ったモディファイアを完全に消去する
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(SPEED_MODIFIER_UUID);
        }

        AttributeInstance attackAttribute = entity.getAttribute(Attributes.ATTACK_SPEED);
        if (attackAttribute != null) {
            attackAttribute.removeModifier(ATTACK_MODIFIER_UUID);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public UnderMoonEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }
}
