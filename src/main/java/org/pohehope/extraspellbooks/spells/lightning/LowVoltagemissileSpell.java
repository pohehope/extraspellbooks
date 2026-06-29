package org.pohehope.extraspellbooks.spells.lightning;

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
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceProjectile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.pohehope.extraspellbooks.utils.helper.ResourceLocationHelper;

import java.util.List;

public class LowVoltagemissileSpell extends AbstractSpell {

    @Override
    public ResourceLocation getSpellResource() {
        return ResourceLocationHelper.getResouceLocation("lowvoltagemissile");
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
                .setMinRarity(SpellRarity.COMMON)
                .setCooldownSeconds(10)
                .setMaxLevel(18)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    public LowVoltagemissileSpell() {
        this.baseManaCost = 50;
        this.baseSpellPower = 4;
        this.manaCostPerLevel = 10;
        this.spellPowerPerLevel = 1;
        //this.castTime = 1;
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return 2 + spellLevel;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1)));
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, this, 48, 12.0f, false);
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData magicData) {
        if (!magicData.getPlayerRecasts().hasRecastForSpell(getSpellId())) {
            magicData.getPlayerRecasts().addRecast(new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, caster), 80, castSource, null), magicData);
        }
        LightningLanceProjectile lance = new LightningLanceProjectile(level, caster);
        float damage = getSpellPower(spellLevel, caster);
        Vec3 pos = caster.getEyePosition();
        lance.setPos(pos.x, pos.y, pos.z);
        lance.setDamage(damage);

        LivingEntity target = null;
        if (magicData.getAdditionalCastData() instanceof TargetEntityCastData castData) {
            if (level instanceof ServerLevel serverLevel) {
                target = castData.getTarget(serverLevel);
            }}

        Vec3 direction;
        if (target != null && target.isAlive()) {
            Vec3 to = target.getBoundingBox().getCenter();
            direction = to.subtract(pos).normalize();
            lance.setHomingTarget(target);
        } else {
            direction = caster.getLookAngle();
        }

        lance.shoot(direction.scale(3.5));
        level.addFreshEntity(lance);
        }
    }
