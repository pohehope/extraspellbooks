package org.pohehope.extraspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleProjectile;
import io.redspace.ironsspellbooks.entity.spells.snowball.Snowball;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.spells.ice.SnowballSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

import java.util.List;

public class FrostDashSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("frost_dash");
    }
    //微設定
    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)//属性
                .setMinRarity(SpellRarity.RARE)//最低レア度
                .setCooldownSeconds(60)//クールダウン
                .setMaxLevel(6).build();//最大レベル
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT; //INSTANT:即時実行　LONG：詠唱実行　CONTINUOUS：伸ばし実行
    }

    public FrostDashSpell() {
        this.baseManaCost = 50; //消費マナ
        this.baseSpellPower = 1;
        this.manaCostPerLevel = 10;
        this.spellPowerPerLevel = 1;
    }

    @Override
    public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
        if (castData instanceof ImpulseCastData bdcd) {
            entity.hasImpulse = bdcd.hasImpulse;
            entity.setDeltaMovement(entity.getDeltaMovement().add(bdcd.x, bdcd.y, bdcd.z));
        }

        super.onClientCast(level, spellLevel, entity, castData);
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new ImpulseCastData();
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        caster.hasImpulse = true;

        // multiplier（倍率）をマイルドに調整（例：ベース1.2 ＋ レベルごとに0.1追加）
        // 以前の 15+ は数値が大きすぎたため、ここを調整してください
        float multiplier = 1.2f + (spellLevel * 0.1f);

        Vec3 forward = caster.getLookAngle();
        if (playerMagicData.getAdditionalCastData() instanceof FrostDashDirectionOverrideCastData) {
            if (Utils.random.nextBoolean())
                forward = forward.yRot(90);
            else
                forward = forward.yRot(-90);
        }

        // 1. まず純粋な「水平方向のダッシュ向き」を計算して、multiplierを掛ける
        Vec3 horizontalDash = new Vec3(forward.x, 0, forward.z).normalize().scale(multiplier);

        // 2. 基本の上向きの力を設定 (例: 0.2)
        double upwardForce = 0.2;

        // 地上にいる場合は少しだけ上空に浮かせる
        if (caster.onGround()) {
            caster.setPos(caster.position().add(0, 0.1, 0)); // 1.5マスワープは高すぎるので0.1程度に
            upwardForce += 0.15; // 地上なら少し上向きの力を足す
        }

        // 最終的なベクトルを合成
        Vec3 vec = new Vec3(horizontalDash.x, upwardForce, horizontalDash.z);

        // ImpulseCastDataへ保存
        playerMagicData.setAdditionalCastData(new ImpulseCastData((float) vec.x, (float) vec.y, (float) vec.z, true));

        // 現在の速度とブレンド（Mth.lerpの第一引数0.75fは、vec側にかなり引っ張られます）
        caster.setDeltaMovement(new Vec3(
                Mth.lerp(.75f, caster.getDeltaMovement().x, vec.x),
                Mth.lerp(.75f, caster.getDeltaMovement().y, vec.y),
                Mth.lerp(.75f, caster.getDeltaMovement().z, vec.z)
        ));

        caster.invulnerableTime = 40;
        playerMagicData.getSyncedData().setSpinAttackType(SpinAttackType.RIPTIDE);

        // --- ここから弾の生成コード ---
        if (!level.isClientSide) {
            Vec3 startPos = caster.position(); // ダッシュ開始時の位置
            Vec3 finalForward = forward.normalize();

            for (int i = 0; i < 10; i++) {
                int delayTicks = i * 2; // 10ティック（0.5秒）だと少し間隔が広いかもなので、2〜4くらいが弾幕感出ます！

                // 【ここが最大のポイント】
                // ループの中で、あらかじめ「未来の弾が出るべき座標」を計算して final 変数に入れる
                // 例：開始地点から、視線方向に「i * 1.2マス」進んだ位置
                final Vec3 spawnPos = startPos.add(finalForward.scale(i * 1.2));

                level.getServer().tell(new net.minecraft.server.TickTask(level.getServer().getTickCount() + delayTicks, () -> {
                    // ※ Snowball（雪玉）自体には setDamage メソッドがないので、
                    // Iron's Spells の IceSpikeEntity や FireboltEntity、あるいはカスタムの弾と仮定します
                    Snowball icicleProjectile = new Snowball(level, caster);

                    // あらかじめ保存しておいた「軌道上の座標」にセットする（casterの位置に依存しない）
                    icicleProjectile.setPos(spawnPos.x, spawnPos.y + 0.5, spawnPos.z);

                    float damage = 4.0f + (spellLevel * 2.0f);
                    icicleProjectile.setDamage(getDuration(spellLevel, caster));
                    icicleProjectile.setExplosionRadius(getRadius(spellLevel, caster));

                    // 弾の下方向（-10）へのベクトルを射出
                    icicleProjectile.shoot(finalForward.x, -10, finalForward.z, 2.0f, 0.0f);

                    level.addFreshEntity(icicleProjectile);
                }));
            }
        }
// --- ここまで ---
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    public float getRadius(int spellLevel, LivingEntity caster) {
        return 1.2f;
    }

    public float getDuration(int spellLevel, LivingEntity caster) {
        return 200 * Mth.sqrt(getEntityPowerMultiplier(caster));
    }

    public static class FrostDashDirectionOverrideCastData implements ICastData {
        @Override
        public void reset() {

        }
    }
}
