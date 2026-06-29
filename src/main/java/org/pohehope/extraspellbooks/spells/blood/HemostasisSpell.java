package org.pohehope.extraspellbooks.spells.blood;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.pohehope.extraspellbooks.Extraspellbooks;
import org.pohehope.extraspellbooks.registry.ModEffects;

public class HemostasisSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocation.fromNamespaceAndPath(Extraspellbooks.MODID, "hemostasis");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
                .setMinRarity(SpellRarity.LEGENDARY)
                .setCooldownSeconds(260)
                .setMaxLevel(4)
                .build();
    }

    public HemostasisSpell() {
        this.baseManaCost = 260;
        this.baseSpellPower = 1;
        this.manaCostPerLevel = 10;
        this.spellPowerPerLevel = 1;
        this.castTime = 40;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(ModEffects.HEMOSTASIS.get(), (int) (getSpellPower(spellLevel, entity) * 300), spellLevel - 1, true, false, true));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
