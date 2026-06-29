package org.pohehope.extraspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleProjectile;
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class SwingSnowBallSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("swing_snow_ball");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMinRarity(SpellRarity.UNCOMMON)
                .setMaxLevel(8)
                .setCooldownSeconds(10)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    public SwingSnowBallSpell() {
        this.baseManaCost = 10;
        this.baseSpellPower = 2;
        this.manaCostPerLevel = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 25;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, this, 48, 1f, false);
        return true;
    }
    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData magicData) {
        if (!magicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            magicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, caster), 80, castSource, null), magicData);
        }

        // 弾を生成・発射する処理はサーバーサイドでのみ実行する
        if (level instanceof ServerLevel serverLevel) {
            float damage = getSpellPower(spellLevel, caster);

            // 事前ターゲットの取得
            LivingEntity targetEntity = null;
            if (magicData.getAdditionalCastData() instanceof TargetEntityCastData castData) {
                targetEntity = castData.getTarget(serverLevel);
            }
            final LivingEntity finalTarget = targetEntity;

            // 3発連射するためのループ（i = 0, 1, 2）
            for (int i = 0; i < 10; i++) {
                // 1発あたり何ティックずらすか（例：4ティック＝約0.2秒間隔）
                int delayTicks = i * 4;

                serverLevel.getServer().tell(new net.minecraft.server.TickTask(serverLevel.getServer().getTickCount() + delayTicks, () -> {
                    // 詠唱者が死んでいたらそれ以降の弾は出さない安全チェック
                    if (caster == null || !caster.isAlive()) {
                        return;
                    }

                    IcicleProjectile icicle = new IcicleProjectile(serverLevel, caster);
                    Vec3 pos = caster.getEyePosition();
                    icicle.setPos(pos.x, pos.y, pos.z);

                    Vec3 direction;
                    // 弾が生成された瞬間にターゲットがまだ生きていれば追尾
                    if (finalTarget != null && finalTarget.isAlive()) {
                        Vec3 to = finalTarget.getBoundingBox().getCenter();
                        direction = to.subtract(pos).normalize();
                        icicle.setHomingTarget(finalTarget);
                    } else {
                        // ターゲットがいない、または既に死んでいる場合はその瞬間の詠唱者の視線方向
                        direction = caster.getLookAngle();
                    }

                    icicle.setExplosionRadius(damage);
                    icicle.setDamage(damage);
                    icicle.shoot(direction.scale(0.5));
                    serverLevel.addFreshEntity(icicle);
                }));
            }
        }
    }
}
