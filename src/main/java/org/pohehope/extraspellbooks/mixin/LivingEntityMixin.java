package org.pohehope.extraspellbooks.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.pohehope.extraspellbooks.entity.mobs.FromazenEntity;
import org.pohehope.extraspellbooks.registry.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract void setHealth(float setdamage);

    @Shadow
    public abstract float getHealth();

    @ModifyVariable(method = "setHealth", at = @At("HEAD"), argsOnly = true)
    private float setHealthCap(float amount) {
        LivingEntity target = (LivingEntity) (Object) this;
            var effectInstance = target.getEffect(ModEffects.HEMOSTASIS.get());
            if (effectInstance != null) {
                int level = effectInstance.getAmplifier() + 1;
                float maxdamage = 0;
                if (5f - level < 0.5f) {
                    maxdamage = 1f;
                } else {
                    maxdamage = 5f - level;
                }
                float capHealth = target.getHealth() - maxdamage;
                if (amount < capHealth) {
                    return capHealth;
                }
            }
        return amount;
    }

    // 💡 もしターゲットのクラスが LivingEntity の Mixin なら、引数の target チェックを FromazenEntity にします
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float modifyBossDamageCap(float amount, DamageSource source) {
        // 💡 このMixinがLivingEntityに対するもの、かつ自分自身がFromazenEntityの場合
        if ((Object) this instanceof FromazenEntity boss) {
            // 1. 1回の攻撃で受ける最大ダメージの上限（ダメージキャップ）を設定
            float maxDamageCap = 15.0f;
            // 2. 特殊なダメージ（奈落の落下、/kill コマンドなど）はキャップを貫通させるセーフティ
            if (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL)) {
                return amount;
            }
            // 3. もし受けるダメージが上限（15）を超えていたら、上限値（15）に丸める
            if (amount > maxDamageCap) {
                return maxDamageCap;
            }
        }
        return amount;
    }
}
