package org.pohehope.extraspellbooks.spells.nature;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.Extraspellbooks;
import org.pohehope.extraspellbooks.entity.spells.carrots.Carrots;

import java.util.List;

public class CarrotsSpell extends AbstractSpell {

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

    public CarrotsSpell() {
        this.baseManaCost = 300;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.manaCostPerLevel = 50;
        this.castTime = 100;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return new ResourceLocation(Extraspellbooks.MODID, "carrots");
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(spellLevel, caster), 1))
        );
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .15f, false);
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        int rings = getRings(spellLevel, entity);
        int count = 2;
        Vec3 center = null;
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
            var target = castTargetingData.getTarget((ServerLevel) level);
            if (target != null)
                center = target.position();
        }
        if (center == null) {
            center = Utils.raycastForEntity(level, entity, 48, true, .15f).getLocation();
            center = Utils.moveToRelativeGroundLevel(level, center, 6);
        }

        for (int r = 0; r < rings; r++) {
            float carrot = count + r * 2;
            for (int i = 0; i < carrot; i++) {
                Vec3 random = new Vec3(Utils.getRandomScaled(1), Utils.getRandomScaled(1), Utils.getRandomScaled(1));
                Vec3 spawn = center.add(new Vec3(0, 0, 1.3 * (r + 1)).yRot(((6.281f / carrot) * i))).add(random);

                spawn = Utils.moveToRelativeGroundLevel(level, spawn, 8);
                if (!level.getBlockState(BlockPos.containing(spawn).below()).isAir()) {
                    Carrots carrots = new Carrots(level, entity, getSpellPower(spellLevel, entity));
                    carrots.moveTo(spawn);
                    carrots.setYRot(Utils.random.nextInt(360));
                    level.addFreshEntity(carrots);
                }
            }
        }
        //In order to trigger sculk sensors
        level.gameEvent(null, GameEvent.ENTITY_ROAR, center);
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
    private int getRings(int spellLevel, LivingEntity entity) {
        return 1 + spellLevel;
    }

}
