package org.pohehope.extraspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.snowball.Snowball;
import io.redspace.ironsspellbooks.spells.ice.SnowballSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class SnowBallFallSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("fall_snowball");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMinRarity(SpellRarity.EPIC)
                .setMaxLevel(5)
                .setCooldownSeconds(10)
                .build();
    }

    public SnowBallFallSpell() {
        this.baseManaCost = 100;
        this.baseSpellPower = 5;
        this.manaCostPerLevel = 0;
        this.spellPowerPerLevel = 2;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);

        if (level instanceof ServerLevel serverLevel) {
            LivingEntity targetEntity = null;
            if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
                targetEntity = targetEntityCastData.getTarget(serverLevel);
            }

            int totalWaves = (int) getSpellPower(spellLevel, caster);
            int count = (int) (getSpellPower(spellLevel, caster) / 2);

            final LivingEntity finalTarget = targetEntity;

            for (int wave = 0; wave < totalWaves; wave++) {
                int delayTicks = wave * 2;

                // net.minecraft.server.TickTask を使用
                serverLevel.getServer().tell(new net.minecraft.server.TickTask(serverLevel.getServer().getTickCount() + delayTicks, () -> {

                    // 【修正】サーバーや詠唱者、ターゲットがいるワールドが正常に存在しているかチェック
                    if (caster == null || !caster.isAlive()) {
                        return;
                    }

                    var random = serverLevel.getRandom();

                    for (int i = 0; i < count; i++) {
                        double x = (random.nextDouble() - 0.5) * 12.0;
                        double z = (random.nextDouble() - 0.5) * 12.0;

                        Vec3 spawnPos;
                        if (finalTarget != null && finalTarget.isAlive()) {
                            spawnPos = finalTarget.position().add(x, 10, z);
                        } else {
                            spawnPos = caster.position().add(x, 10, z);
                        }

                        Snowball snowball = new Snowball(serverLevel, caster);
                        snowball.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                        snowball.setDamage(getDuration(spellLevel, caster) * 5);
                        snowball.setExplosionRadius(getRadius(spellLevel, caster));
                        snowball.setDeltaMovement(new Vec3(0, -0.5, 0));
                        serverLevel.addFreshEntity(snowball);
                    }
                }));
            }
        }
    }
    public float getRadius(int spellLevel, LivingEntity caster) {
        return 3.5f + spellLevel * .5f;
    }

    public float getDuration(int spellLevel, LivingEntity caster) {
        return 200 * Mth.sqrt(getEntityPowerMultiplier(caster));
    }
}
