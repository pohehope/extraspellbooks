package org.pohehope.extraspellbooks.spells.night;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.pohehope.extraspellbooks.registry.ModEffects;
import org.pohehope.extraspellbooks.registry.SchoolRegistry;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class UnderMoonSpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("undermoon");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.NIGHT_RESOURCE)//属性
                .setMinRarity(SpellRarity.EPIC)//最低レア度
                .setCooldownSeconds(60)//クールダウン
                .setMaxLevel(4).build();//最大レベル;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    public UnderMoonSpell() {
        this.baseManaCost = 50; //消費マナ
        this.baseSpellPower = 1;
        this.manaCostPerLevel = 20;
        this.spellPowerPerLevel = 1;
        this.castTime = 100; //実行されるまでの時間(INSTANT以外)
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(ModEffects.UNDERMOON.get(), (int) (getSpellPower(spellLevel, entity) * 540), spellLevel - 1, false, false, true));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
