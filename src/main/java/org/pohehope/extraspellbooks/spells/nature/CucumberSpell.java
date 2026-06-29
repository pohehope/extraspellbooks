package org.pohehope.extraspellbooks.spells.nature;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.entity.spells.cucumber.Cucumber;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

import java.util.List;


public class CucumberSpell extends AbstractSpell {

    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("cucumber");
    }



    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
                .setMinRarity(SpellRarity.COMMON)
                .setCooldownSeconds(20)
                .setMaxLevel(12)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    public CucumberSpell() {
        this.baseManaCost = 30;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 2;
        this.castTime = 10;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1))
                );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
        Cucumber cucumber = new Cucumber(level, caster);
        float damage = getSpellPower(spellLevel, caster) - 10;
        Vec3 pos = caster.getEyePosition();
        cucumber.setPos(pos.x, pos.y, pos.z);
        cucumber.setDamage(damage);
        caster.heal(damage/4);

        cucumber.shoot(caster.getLookAngle());
        level.addFreshEntity(cucumber);
    }
}
