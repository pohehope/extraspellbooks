package org.pohehope.extraspellbooks.spells.fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.effect.BurningDashEffect;
import io.redspace.ironsspellbooks.effect.ChargeEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.pohehope.extraspellbooks.Extraspellbooks;
import org.pohehope.extraspellbooks.registry.ModEffects;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

import java.util.List;

public class BurningSoulSpell extends AbstractSpell {
    //spellID
    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("burningsoul");
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 20, 1)));
    }
//微設定
    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)//属性
                .setMinRarity(SpellRarity.RARE)//最低レア度
                .setCooldownSeconds(60)//クールダウン
                .setMaxLevel(5).build();//最大レベル
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT; //INSTANT:即時実行　LONG：詠唱実行　CONTINUOUS：伸ばし実行
    }


    public BurningSoulSpell() {
        this.baseManaCost = 220; //消費マナ
        this.baseSpellPower = 240;
        //this.castTime = 1; //実行されるまでの時間(INSTANT以外)
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(ModEffects.BURNINGSOUL.get(), (int) (getSpellPower(spellLevel, entity) * 20), spellLevel - 1, false, false, true));
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
