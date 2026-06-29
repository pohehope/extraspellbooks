package org.pohehope.extraspellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class MiniIceBombSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("mini_ice_bomb");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
                .setMinRarity(SpellRarity.UNCOMMON)
                .setMaxLevel(6)
                .setCooldownSeconds(10)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    public MiniIceBombSpell() {
        this.baseManaCost = 10;
        this.baseSpellPower = 2;
        this.manaCostPerLevel = 0;
        this.spellPowerPerLevel = 3;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
        IcicleProjectile icicle = new IcicleProjectile(level, caster);
        icicle.setPos(caster.position().add(0, caster.getEyeHeight() - icicle.getBoundingBox().getYsize() * .5f, 0));
        icicle.setNoGravity(false);
        icicle.shoot(caster.getLookAngle());
        icicle.setDamage(spellLevel);
        level.addFreshEntity(icicle);
    }
}
