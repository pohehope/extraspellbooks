package org.pohehope.extraspellbooks.entity.spells.plasmadamagebox;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.pohehope.extraspellbooks.registry.ModEntityRegistry;
import org.pohehope.extraspellbooks.registry.Modspellregistry;

import java.util.Optional;
import java.util.function.Supplier;

public class PlasmaDamageBoxEntity extends AbstractMagicProjectile {
    public PlasmaDamageBoxEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    public PlasmaDamageBoxEntity(Level level, LivingEntity owner, float damage) {
        this(ModEntityRegistry.DAMAGE_BOX.get(),level);
        setOwner(owner);
        setDamage(damage);
    }

    private float damage;
    private int age;

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            MagicManager.spawnParticles(level(), ParticleHelper.ELECTRIC_SPARKS, xo, yo, zo, 5, .08, .08, .08, 0.3, true);
            if (age > 20) {
                this.discard();
            }else {
                level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1.2)).forEach(this::dealDamage);
            }
        }
        age++;
    }

    public boolean dealDamage(LivingEntity target) {
        if (target != getOwner())
            if (DamageSources.applyDamage(target, damage, Modspellregistry.PLASMA_STEP.get().getDamageSource(this, getOwner()))) {
                return true;
            }
        return false;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        DamageSources.applyDamage(pResult.getEntity(), damage, Modspellregistry.PLASMA_STEP.get().getDamageSource(this, getOwner()));
    }

    @Override
    protected void onHit(HitResult hitresult) {
        super.onHit(hitresult);
    }

    @Override
    public void trailParticles() {
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {
    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public Optional<Supplier<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }
}
