package org.pohehope.extraspellbooks.spells.ice;

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
import org.pohehope.extraspellbooks.entity.spells.frostenstrosity.FrostEnstrosityEntity;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

public class FrostEnstrositySpell extends AbstractSpell {
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("frost_enstrosity");
    }
    //微設定
    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)//属性
                .setMinRarity(SpellRarity.LEGENDARY)//最低レア度
                .setCooldownSeconds(60)//クールダウン
                .setMaxLevel(4).build();//最大レベル
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG; //INSTANT:即時実行　LONG：詠唱実行　CONTINUOUS：伸ばし実行
    }

    public FrostEnstrositySpell() {
        this.baseManaCost = 300; //消費マナ
        this.baseSpellPower = 5;
        this.manaCostPerLevel = 10;
        this.spellPowerPerLevel = 1;
        this.castTime = 50;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
        float damage = 10 * spellLevel; // 魔法自体のダメージ計算

        FrostEnstrosityEntity frostEnstrosity = new FrostEnstrosityEntity(level, caster, damage, spellLevel);
        frostEnstrosity.setDamage(spellLevel * 3);
        frostEnstrosity.setPos(caster.position());
        frostEnstrosity.setOwner(caster);
        level.addFreshEntity(frostEnstrosity);
    }
}
