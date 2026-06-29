package org.pohehope.extraspellbooks.spells.holy;

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
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class SuperNovaSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("super_nova");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
                .setMinRarity(SpellRarity.EPIC)
                .setMaxLevel(6)
                .setCooldownSeconds(120)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    public SuperNovaSpell() {
        this.baseManaCost = 400;
        this.baseSpellPower = 1;
        this.manaCostPerLevel = 100;
        this.spellPowerPerLevel = 10;
        this.castTime = 0;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
