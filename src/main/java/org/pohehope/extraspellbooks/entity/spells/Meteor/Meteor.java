package org.pohehope.extraspellbooks.entity.spells.Meteor;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.pohehope.extraspellbooks.registry.ModEntityRegistry;
import org.pohehope.extraspellbooks.registry.Modspellregistry;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class Meteor extends AbstractMagicProjectile {

    private LivingEntity owner;
    private UUID ownerUUID;
    private float damage;
    private float speed;
    private int age;

    public Meteor(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Meteor(Level level, LivingEntity owner, float damage) {
        this(ModEntityRegistry.METEOR.get(), level);
        setOwner(owner);
        setDamage(damage);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        DamageSources.applyDamage(entityHitResult.getEntity(), damage, Modspellregistry.METEORSHOWER.get().getDamageSource(this, getOwner()));
    }

    @Override
    protected void onHit(HitResult hitresult) {
        super.onHit(hitresult);
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity) entity;
            }
        }

        return this.owner;
    }

    @Override
    public void trailParticles() {
        Vec3 vec3 = this.position().subtract(getDeltaMovement().scale(.5f));
        level().addParticle(ParticleHelper.COMET_FOG, vec3.x, vec3.y, vec3.z, 0,0,0);
    }

    @Override
    public void impactParticles(double v, double v1, double v2) {
        MagicManager.spawnParticles(level(), ParticleHelper.ENDER_SPARKS, xo, yo, zo, 12, 1, 1,1, .5, true);
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public void setNoGravity(boolean noGravity) {
        super.setNoGravity(noGravity);
    }

    @Override
    public Optional<Supplier<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    public int getAge(){
        return tickCount;
    }
}
