package org.pohehope.extraspellbooks.spells.fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.pohehope.extraspellbooks.Extraspellbooks;

public class ExplosiveDroneSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return new ResourceLocation(Extraspellbooks.MODID, "explosiveDrone");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
                .setMinRarity(SpellRarity.RARE)
                .setMaxLevel(12)
                .setCooldownSeconds(30)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    public ExplosiveDroneSpell() {
        this.baseManaCost = 20; //消費マナ
        this.baseSpellPower = 10;
        this.spellPowerPerLevel = 2;
        this.manaCostPerLevel = 10;
        this.castTime = 50;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
