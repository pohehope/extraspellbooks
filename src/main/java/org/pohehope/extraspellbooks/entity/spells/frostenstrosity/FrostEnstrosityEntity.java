package org.pohehope.extraspellbooks.entity.spells.frostenstrosity;

import com.github.L_Ender.cataclysm.init.ModEffect;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.pohehope.extraspellbooks.registry.ModEntityRegistry;
import org.pohehope.extraspellbooks.registry.Modspellregistry;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class FrostEnstrosityEntity extends AbstractMagicProjectile {
        public FrostEnstrosityEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
            super(pEntityType, pLevel);
            this.setNoGravity(true);
        }

        private float damage;
        private int age;
        private int spellLevel = 1; // ★魔法レベルを保持する変数を追加

        // ★コンストラクタの引数を4つ（level, owner, damage, spellLevel）に変更
        public FrostEnstrosityEntity(Level level, LivingEntity owner, float damage, int spellLevel) {
            this(ModEntityRegistry.FROST_ENSTROSITY.get(), level);
            setOwner(owner);
            setDamage(damage);
            this.spellLevel = spellLevel; // ★ここで魔法レベルをエンティティに覚えさせる！
        }

    @Override
        public void tick() {
            super.tick();
            if (!level().isClientSide) {
                if (age > 1400) {
                    this.discard();
                }
                Entity owner = this.getOwner();

                // ★保存した spellLevel を使って範囲を計算する（例: 基本4.0 + レベル×2.0）
                double damage1Ring = 4.0 + (this.spellLevel * 2.0);

                AABB setDamage1Ring = this.getBoundingBox().inflate(damage1Ring);
                List<LivingEntity> targetEntites = this.level().getEntitiesOfClass(
                        LivingEntity.class,
                        setDamage1Ring
                );
                for (LivingEntity target : targetEntites) {
                    // ★ ターゲットが魔法のオーナー（自分自身）なら、処理をスキップする
                    if (target == this.getOwner()) {
                        continue;
                    }

                    DamageSources.applyDamage(target, getDamage(), Modspellregistry.FROST_ENSTROSITY.get().getDamageSource(this, getOwner()));
                    target.setTicksFrozen(target.getTicksFrozen() + 5);
                }
            }
            age++;
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
