package org.pohehope.extraspellbooks.spells.night;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.eldritch_blast.EldritchBlastVisualEntity;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.entity.spells.Meteor.Meteor;
import org.pohehope.extraspellbooks.registry.SchoolRegistry;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

import java.util.concurrent.TimeUnit;

public class MeteorShowerSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("meteor_shower");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.NIGHT_RESOURCE)
                .setMinRarity(SpellRarity.EPIC)
                .setMaxLevel(4)
                .setCooldownSeconds(25)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    public MeteorShowerSpell() {
        this.baseManaCost = 550;
        this.baseSpellPower = 1;
        this.manaCostPerLevel = 50;
        this.spellPowerPerLevel = 2;
        this.castTime = 10;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, this, 48, 1f, false);
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        LivingEntity targetEntity = null;
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
            if (level instanceof ServerLevel serverLevel) {
                targetEntity = targetEntityCastData.getTarget(serverLevel);
            }
        }

        if (targetEntity == null) {
            super.onCast(level, spellLevel, caster, castSource, playerMagicData);
            return;
        }

        var random = level.getRandom();
        for (int i = 0; i < getSpellPower(spellLevel, caster)*2; i++) {
            double x = (random.nextDouble() - .2) * 6f;
            double y = targetEntity.getBbHeight() + 5.0 + random.nextDouble() * 1.0f;
            double z = (random.nextDouble() - .2) * 6f;
            Vec3 SpawnPos = caster.position().add(x,y,z);
            Vec3 EndPos = targetEntity.position();
            Vec3 direction = EndPos.subtract(SpawnPos).normalize();

            Meteor meteor = new Meteor(level, caster, spellLevel);
            meteor.setPos(SpawnPos);
            meteor.setOwnerUUID(caster.getUUID());
            meteor.setDamage(spellLevel);
            meteor.setSpeed((float) (random.nextFloat() - .2) * .5f);
            meteor.shoot(direction.x * 1.2, direction.y, direction.z * 1.2, 2.5f, 0.0f);
            level.addFreshEntity(meteor);

            MagicManager.spawnParticles(level, ParticleHelper.ENDER_SPARKS, EndPos.x, EndPos.y, EndPos.z, 50, 0, 0, 0, .3f, false);

        }
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }
}
