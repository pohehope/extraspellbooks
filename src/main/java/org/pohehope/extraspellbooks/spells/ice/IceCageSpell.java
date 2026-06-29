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
import io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombEntity;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class IceCageSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("ice_cage");
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

    public IceCageSpell() {
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
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        LivingEntity target = null;
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castData) {
            if (level instanceof ServerLevel serverLevel) {
                target = castData.getTarget(serverLevel);
            }}
        if (target == null) {
            super.onCast(level, spellLevel, entity, castSource, playerMagicData);
            return;
        }
        IceTombEntity iceTombEntity = new IceTombEntity(level, target);
        iceTombEntity.moveTo(target.position());
        iceTombEntity.setDeltaMovement(target.getDeltaMovement());
        level.addFreshEntity(iceTombEntity);
        target.startRiding(iceTombEntity, true);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
