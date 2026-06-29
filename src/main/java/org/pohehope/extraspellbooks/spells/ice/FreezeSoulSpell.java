package org.pohehope.extraspellbooks.spells.ice;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.pohehope.extraspellbooks.registry.ModEffects;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

import java.util.List;

public class FreezeSoulSpell extends AbstractSpell {
    //spellID
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("freezesoul");
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 240, 1)));
    }
    //微設定
    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.ICE_RESOURCE)//属性
                .setMinRarity(SpellRarity.RARE)//最低レア度
                .setCooldownSeconds(60)//クールダウン
                .setMaxLevel(9).build();//最大レベル
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT; //INSTANT:即時実行　LONG：詠唱実行　CONTINUOUS：伸ばし実行
    }


    public FreezeSoulSpell() {
        this.baseManaCost = 50; //消費マナ
        this.baseSpellPower = 1;
        this.manaCostPerLevel = 10;
        this.spellPowerPerLevel = 1;
        //this.castTime = 1; //実行されるまでの時間(INSTANT以外)
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(ModEffects.FREEZESOUL.get(), (int) (getSpellPower(spellLevel, entity) * 240), spellLevel - 1, false, false, true));
        entity.heal(getSpellPower(spellLevel, entity));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
