package org.pohehope.extraspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.Extraspellbooks;
import org.pohehope.extraspellbooks.entity.spells.snowflake.SnowFlake;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

import java.util.logging.Logger;

public class SnowFlakeSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("snow_flake");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMinRarity(SpellRarity.RARE)
                .setMaxLevel(8)
                .setCooldownSeconds(10)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    public SnowFlakeSpell() {
        this.baseManaCost = 10;
        this.baseSpellPower = 2;
        this.manaCostPerLevel = 0;
        this.spellPowerPerLevel = 3;
        this.castTime = 25;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, this, 48, 1f, false);
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
        LivingEntity targetEntity = null;
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
            if (level instanceof ServerLevel serverLevel) {
                targetEntity = targetEntityCastData.getTarget(serverLevel);
            }
        }
        var random = level.getRandom();
        for (int i = 0; i < getSpellPower(spellLevel * 2, caster); i++) {
            SnowFlake snowFlake = new SnowFlake(level, caster, spellLevel + 2);
            double x = (random.nextDouble());
            double y = (random.nextDouble());
            Vec3 spawnPos = caster.position().add(x,y,5);
            Vec3 direction;
            if (targetEntity != null && targetEntity.isAlive()) {
                Vec3 to = targetEntity.getBoundingBox().getCenter();
                direction = to.subtract(spawnPos).normalize();
            } else {
                direction = caster.getLookAngle();
            }
            snowFlake.setPos(spawnPos);
            snowFlake.setSpeed(4);
            snowFlake.shoot(direction);
            level.addFreshEntity(snowFlake);
        }
    }
}
