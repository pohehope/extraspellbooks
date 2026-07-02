package org.pohehope.extraspellbooks.spells.lightning;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.capabilities.magic.ImpulseCastData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.pohehope.extraspellbooks.entity.spells.plasmadamagebox.PlasmaDamageBoxEntity;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

import static io.redspace.ironsspellbooks.spells.ender.TeleportSpell.findTeleportLocation;

public class PlasmaStepSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("plasma_step");
    }
    //微設定
    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)//属性
                .setMinRarity(SpellRarity.RARE)//最低レア度
                .setCooldownSeconds(60)//クールダウン
                .setMaxLevel(6).build();//最大レベル
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT; //INSTANT:即時実行　LONG：詠唱実行　CONTINUOUS：伸ばし実行
    }

    public PlasmaStepSpell() {
        this.baseManaCost = 50; //消費マナ
        this.baseSpellPower = 1;
        this.manaCostPerLevel = 10;
        this.spellPowerPerLevel = 2;
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 3;
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
        var teleportData = (TeleportData) playerMagicData.getAdditionalCastData();
        Vec3 dest = null;
        if (teleportData != null) {
            var potentinalTarget = teleportData.getTeleportTargetPosition();
            if (potentinalTarget != null) {
                dest = potentinalTarget;
            }
        }
        if (dest == null) {
            dest = findTeleportLocation(level, caster, getDuration(spellLevel, caster));
        }
        if (caster.isPassenger()) {
            caster.stopRiding();
        }

        if (!level.isClientSide) {
            Vec3 startPos = caster.position();
            double max = 15.0;
            // 1. 開始地点から目的地への「差分ベクトル」を計算
            Vec3 travelVector = dest.subtract(startPos);
            // 2. 移動の総距離を取得
            double totalDistance = travelVector.length();

            if (totalDistance > max) {
                Vec3 limitVec = travelVector.normalize().scale(max);
                dest = startPos.add(limitVec);
            }

            // 距離がほぼ0（その場テレポート）の場合は生成しない、または安全対策
            if (totalDistance > 0.001) {
                // 3. 正規化して「1ブロック分の方向ベクトル」を作る
                Vec3 direction = travelVector.normalize();

                // 例: 10個のエンティティをテレポートの軌跡に等間隔（または1ブロックごと）に配置
                // もし距離に応じて個数を変えたい場合は、10の代わりに (int)totalDistance などにするのもアリです
                int count = (int) Math.min(totalDistance, max);

                double step = 1.0;

                for (int i = 0; i < count; i++) {
                    // 方向ベクトルにステップ長とインデックスを掛けて、徐々に前進させる
                    final Vec3 spawnPos = startPos.add(direction.scale(step * i));

                    level.getServer().tell(new TickTask(level.getServer().getTickCount() + i, () -> {
                        PlasmaDamageBoxEntity plasmaDamageBox = new PlasmaDamageBoxEntity(level, caster, spellLevel);
                        plasmaDamageBox.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                        float damage = getSpellPower(spellLevel, caster);
                        plasmaDamageBox.setDamage(damage);
                        level.addFreshEntity(plasmaDamageBox);
                    }));
                }
            }
        }

        // 最後にテレポートを実行
        caster.teleportTo(dest.x, dest.y, dest.z);
        caster.resetFallDistance();

        playerMagicData.resetAdditionalCastData();
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    public float getDuration(int spellLevel, LivingEntity caster) {
        return 200 * Mth.sqrt(getEntityPowerMultiplier(caster));
    }

    public static class TeleportData implements ICastData {
        private Vec3 teleportTargetPosition;

        public TeleportData(Vec3 teleportTargetPosition) {
            this.teleportTargetPosition = teleportTargetPosition;
        }

        public void setTeleportTargetPosition(Vec3 targetPosition) {
            this.teleportTargetPosition = targetPosition;
        }

        public Vec3 getTeleportTargetPosition() {
            return this.teleportTargetPosition;
        }

        @Override
        public void reset() {
            //Nothing needed here for teleport
        }
    }

    public static class PlasmaStepDirectionOverrideCastData implements ICastData {
        @Override
        public void reset() {

        }
    }
}
