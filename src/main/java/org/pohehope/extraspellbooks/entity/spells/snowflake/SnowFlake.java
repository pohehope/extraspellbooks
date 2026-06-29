package org.pohehope.extraspellbooks.entity.spells.snowflake;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.pohehope.extraspellbooks.registry.ModEntityRegistry;
import org.pohehope.extraspellbooks.registry.Modspellregistry;

import java.util.Optional;
import java.util.function.Supplier;

public class SnowFlake extends AbstractMagicProjectile {
    public SnowFlake(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    private LivingEntity owner;
    private float damage;
    private float speed;
    private int age;

    public SnowFlake (Level level, LivingEntity owner, float damage) {
        this(ModEntityRegistry.SNOWFLAKE.get(), level);
        setOwner(owner);
        setDamage(damage);
        setSpeed(speed);
    }

    public void setDamage(float damage) {this.damage = damage;}
    public void setSpeed(float speed) {this.speed = speed;}

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
        }
        age++;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            DamageSources.applyDamage(entityHitResult.getEntity(), damage, Modspellregistry.SNOWFLAKE.get().getDamageSource(this, getOwner()));
            MobEffects.MOVEMENT_SLOWDOWN.applyEffectTick((LivingEntity) entityHitResult.getEntity(), 20);
            entityHitResult.getEntity().setTicksFrozen(1400);
        }
    }

    @Override
    protected void onHit(HitResult hitresult) {
        super.onHit(hitresult);
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public void trailParticles() {
        Vec3 vec3 = this.position().subtract(getDeltaMovement().scale(.5f));
        level().addParticle(ParticleHelper.SNOW_DUST, vec3.x, vec3.y, vec3.z, 0,0,0);
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {
        MagicManager.spawnParticles(level(), ParticleHelper.SNOWFLAKE, xo, yo, zo, 12, 1, 1,1, .5, true);
    }

    @Override
    public float getSpeed() {return speed - age;}
    public int getAge(){return tickCount;}

    @Override
    public Optional<Supplier<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }
}
