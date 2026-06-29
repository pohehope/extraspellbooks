package org.pohehope.extraspellbooks.entity.mobs;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pohehope.extraspellbooks.registry.Modspellregistry;

import java.util.*;

public class FromazenEntity extends AbstractSpellCastingMob implements Enemy {

    private static final Random RANDOM = new Random();
    private static final double MAX_HEALTH_VALUE = 375;
    private static final double MOVEMENT_SPEED = 2.75;

    private static final EntityDataAccessor<Integer> CURRENT_ACTION_STATE = SynchedEntityData.defineId(FromazenEntity.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossEvent = (ServerBossEvent) (new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.BLUE,
            BossEvent.BossBarOverlay.PROGRESS
    )).setDarkenScreen(false);

    private final List<ActionPattern> allPatterns = new ArrayList<>();
    private final Map<String, ActionPattern> patternMap = new HashMap<>();

    private ActionPattern actionPattern = null;
    private int actionIndex = 0;
    private int actionTimer = 0;

    public FromazenEntity(EntityType<? extends FromazenEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5000;
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
        this.initPatterns();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CURRENT_ACTION_STATE, 0); // 初期値は 0 (WAIT)
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(false);
        return flyingpathnavigation;
    }

    public static AttributeSupplier.Builder createLivingAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.ARMOR, 7)
                .add(Attributes.MOVEMENT_SPEED, 0.55)
                .add(Attributes.FLYING_SPEED, 0.55)
                .add(Attributes.FOLLOW_RANGE, 128.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    @Override
    public boolean isPushable() {return false;}
    @Override
    protected void doPush(@NotNull Entity entity) {}

    @Override
    public void startSeenByPlayer(ServerPlayer player) { super.startSeenByPlayer(player); this.bossEvent.addPlayer(player); }
    @Override
    public void stopSeenByPlayer(ServerPlayer player) { super.stopSeenByPlayer(player); this.bossEvent.removePlayer(player); }
    @Override
    public void setCustomName(@Nullable Component name) { super.setCustomName(name); this.bossEvent.setName(this.getDisplayName()); }
    @Override
    protected void customServerAiStep() { super.customServerAiStep(); this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth()); }

    // --- データ構造定義（Recordで1行に凝縮） ---
    public record CastEntity(AbstractSpell spell, int customCastTime, int level) {}

    public enum PatternActionType {
        WAIT, MOVE, DOWN, CAST_SPELL, MY_CAST, CAST_MULTI
    }

    // 複雑な引数はファクトリメソッドで生成するように整理
    public record PatternAction(
            PatternActionType type,
            int duration,
            List<AbstractSpell> spells,
            List<CastEntity> castEntities,
            int interval,
            int spellLevel
    ) {
        // デフォルト値を入れるための簡易的なファクトリメソッド群
        public static PatternAction wait(int duration) {
            return new PatternAction(PatternActionType.WAIT, duration, List.of(), List.of(), 0, 0);
        }

        public static PatternAction move(int duration) {
            return new PatternAction(PatternActionType.MOVE, duration, List.of(), List.of(), 0, 0);
        }

        public static PatternAction down(int duration) {
            return new PatternAction(PatternActionType.DOWN, duration, List.of(), List.of(), 0, 0);
        }

        public static PatternAction spell(AbstractSpell spell, int level) {
            return new PatternAction(PatternActionType.CAST_SPELL, 10, List.of(spell), List.of(), 0, level);
        }

        public static PatternAction multiSpell(List<AbstractSpell> spells, int interval) {
            return new PatternAction(PatternActionType.CAST_MULTI, 10, spells, List.of(), interval, 0);
        }
    }

    public record ActionPattern(
            String name,
            int phase,
            double minDist,
            double maxDist,
            List<PatternAction> actions
    ) {
        public ActionPattern(String name, int phase, double minDist, double maxDist, PatternAction... actions) {
            this(name, phase, minDist, maxDist, Arrays.asList(actions));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // 💡 クライアントサイド、または死亡時はパーティクルだけ出して終了
        if (this.level().isClientSide() || !this.isAlive()) {
            spawnActionParticles();
            return;
        }

        if (!this.level().isClientSide && this.isAlive()) {

            // 自分の現在のAABB（当たり判定の箱）を取得
            net.minecraft.world.phys.AABB boundingBox = this.getBoundingBox();

            // 自分のAABBに「接触している」Entity（LivingEntity）をすべてリストアップする
            // ただし自分自身（this）は除外する
            java.util.List<LivingEntity> targets = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    boundingBox,
                    entity -> entity != this && entity.isAlive()
            );

            // 触れているターゲット全員にダメージを与える
            for (LivingEntity target : targets) {
                // ダメージソースの設定（1.20.1ではダメージタイプのシステムが変わったため、level().damageSources() を使います）
                // Mobの直接攻撃（mobAttack）として扱い、自分を原因として設定
                net.minecraft.world.damagesource.DamageSource damageSource = this.level().damageSources().mobAttack(this);

                // 接触ダメージの量（例: 4.0 = ハート2個分）
                // 属性値から取得したい場合は、(float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) でもOKです
                float damageAmount = (float) this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                // ターゲットにダメージを与える
                target.hurt(damageSource, damageAmount);
            }
        }

        this.resetFallDistance(); // 飛行Mobの落下距離リセット

        LivingEntity target = this.getTarget();

        // --- 1. ターゲットがいない（非戦闘時）の処理 ---
        if (target == null) {
            this.actionPattern = null;
            this.entityData.set(CURRENT_ACTION_STATE, 0); // WAIT状態にする

            // 自然回復
            if (this.tickCount % 10 == 0) {
                if (this.getHealth() < this.getMaxHealth()) {
                    float healAmount = 100.0f;
                    this.heal(healAmount);
                }
            }

            // 地面から4マスの高さをホバリング維持
            int groundY = this.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, this.blockPosition().getX(), this.blockPosition().getZ());
            double currentHeightFromGround = this.getY() - groundY;
            if (currentHeightFromGround < 4.0) {
                this.setDeltaMovement(this.getDeltaMovement().x, 0.05, this.getDeltaMovement().z);
            }
            return;
        }

        // --- 2. ターゲット基準の高度維持ロジック（ウィザー風） ---
        double idealHeightAboveTarget = 4.5;
        double tolerance = 0.3;
        double currentYDiff = this.getY() - target.getY();
        net.minecraft.world.phys.Vec3 motion = this.getDeltaMovement();

        // 急降下（DOWN）アクション中でない場合のみ、高さ維持を実行
        boolean isDescending = false;
        if (this.actionPattern != null && this.actionIndex > 0) {
            PatternAction currentAction = this.actionPattern.actions().get(this.actionIndex - 1);
            if (currentAction.type == PatternActionType.DOWN) {
                isDescending = true;
            }
        }

        if (!isDescending) {
            if (currentYDiff > idealHeightAboveTarget + tolerance) {
                this.setDeltaMovement(motion.x, -0.5, motion.z); // 【高すぎる】下降（※-2.0はかなり急なので様子を見て調整してください）
            } else if (currentYDiff < idealHeightAboveTarget - tolerance) {
                this.setDeltaMovement(motion.x, 0.5, motion.z);  // 【低すぎる】上昇
            } else {
                this.setDeltaMovement(motion.x, motion.y * 0.2, motion.z); // 【理想の高さ】安定
            }
        }

        // --- 3. パターンの選択処理 ---
        if (this.actionPattern == null) {
            selectNextPattern(target);
            if (this.actionPattern == null) {
                this.getNavigation().moveTo(target, MOVEMENT_SPEED);
                return;
            }
        }

        // --- 4. タイマーを進める処理（アクション実行中） ---
        if (this.actionTimer > 0) {
            this.actionTimer--;

            // 🛑 【安全対策】もしパターンが途中で null になっていたらリセットして抜ける
            if (this.actionPattern == null) {
                this.actionTimer = 0;
                this.actionIndex = 0;
                return;
            }

            if (this.actionIndex > 0) {
                PatternAction currentAction = this.actionPattern.actions().get(this.actionIndex - 1);
                if (currentAction.type == PatternActionType.MOVE) {
                    this.getNavigation().moveTo(target, MOVEMENT_SPEED);
                }
                else if (currentAction.type == PatternActionType.DOWN) {
                    if (this.getY() > target.getY() + 1.0) {
                        this.setDeltaMovement(this.getDeltaMovement().x, -0.3, this.getDeltaMovement().z);
                    } else {
                        this.setDeltaMovement(this.getDeltaMovement().x, 0, this.getDeltaMovement().z);
                    }
                }
            }
            return;
        }

        // --- 5. タイマーが0の時、次のアクションを実行 ---
        if (this.actionPattern != null && this.actionIndex < this.actionPattern.actions().size()) {
            PatternAction action = this.actionPattern.actions().get(this.actionIndex);
            executeAction(action, target);
            this.actionTimer = action.duration;
            this.actionIndex++;
        } else {
            this.actionPattern = null; // すべてのアクションが終わったらパターンをリセット
        }
    }

    private void selectNextPattern(LivingEntity target) {
        if (target == null) return;

        double distance = this.distanceTo(target);

        // 💡 現在の自分の真下にある地面（地表ブロック）のY座標と、地面からの高さを計算
        int groundY = this.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, this.blockPosition().getX(), this.blockPosition().getZ());
        double heightFromGround = this.getY() - groundY;

        // 登録された全パターンの中から条件に合うものを抽出
        List<ActionPattern> available = this.allPatterns.stream()
                .filter(p -> distance >= p.minDist() && distance <= p.maxDist())
                .filter(p -> {
                    // =================================================================
                    // ❄️ 【修正版】地面からの距離（高度）による魔法（パターン）の制限ルール
                    // =================================================================

                    // 🔽 【地上限定技】地面からの高さが 8.0 マスより高いときは「使えない(false)」
                    if (p.name().equals("snowy_area") && heightFromGround > 8.0) {
                        return false;
                    }
                    if (p.name().equals("ice_spike_min") && heightFromGround > 8.0) {
                        return false;
                    }
                    if (p.name().equals("snow_pan") && heightFromGround > 8.0) {
                        return false;
                    }

                    // 🔽 【空中限定技】地面からの高さが 8.0 マス未満（地上に近い）のときは「使えない(false)」
                    if (p.name().equals("not_snowy_area") && heightFromGround < 8.0) {
                        return false; // 💡 falseに修正（プールから除外する）
                    }
                    if (p.name().equals("not_ice_spike_min") && heightFromGround < 8.0) {
                        return false; // 💡 falseに修正（プールから除外する）
                    }
                    if (p.name().equals("not_snow_pan") && heightFromGround < 8.0) {
                        return false; // 💡 falseに修正（プールから除外する）
                    }

                    // 全ての除外フィルターをすり抜けた技だけが、最終的に候補（true）として残る
                    return true;
                    // =================================================================
                })
                .toList();

        if (!available.isEmpty()) {
            this.actionPattern = available.get(RANDOM.nextInt(available.size()));
            this.actionIndex = 0;
            this.actionTimer = 0;
        } else {
            // 該当するパターンがない（遠すぎる、または高度制限で技が出せない）場合はターゲットに近づく
            this.getNavigation().moveTo(target, MOVEMENT_SPEED);
        }
    }

    private void executeAction(PatternAction action, LivingEntity target) {
        switch (action.type) {
            case WAIT -> {
                this.navigation.stop();
            }
            case MOVE -> {
                this.getNavigation().moveTo(target, MOVEMENT_SPEED);
            }
            case DOWN -> {
                // 💡 降下開始時にナビゲーションを止め、下方向への速度を与える
                this.navigation.stop();
                this.setDeltaMovement(this.getDeltaMovement().x, -0.4, this.getDeltaMovement().z);
            }
            case CAST_SPELL -> {
                if (!action.spells.isEmpty()) {
                    AbstractSpell spell = action.spells.get(0);
                    this.initiateCastSpell(spell, action.spellLevel);
                }
            }
            case MY_CAST -> {
                if (!action.spells.isEmpty()) {
                    AbstractSpell spell = action.spells.get(0);

                    // 💡 1. 現在の本来のターゲット（敵）を一時的に保存しておく
                    LivingEntity originalTarget = this.getTarget();

                    // 💡 2. ターゲットを「自分自身（this）」に強制変更する
                    this.setTarget(this);

                    // 💡 3. 自分をターゲットとして詠唱を開始！
                    // (これによって自分に向けて回復やバフの魔法が発動します)
                    this.initiateCastSpell(spell, action.spellLevel);

                    // 💡 4. 詠唱命令が通ったら、即座にターゲットを元の敵に戻す
                    // (initiateCastSpell内部でターゲット座標へのロックオンは完了しているので、戻しても大丈夫です)
                    this.setTarget(originalTarget);
                }
            }
        }
    }

    // 💡 状態に応じたパーティクル生成メソッド（常時発生エフェクト付き）
    private void spawnActionParticles() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        // =================================================================
        // ✨ 【追加】常時発生するベースパーティクル
        // ボスの胴体〜足元付近から、常にうっすらと雪と冷気が吹き出す
        // =================================================================
        if (RANDOM.nextInt(2) == 0) { // 軽めの負荷にするため2回に1回発生
            // 足元に漂う冷気のモヤ
            this.level().addParticle(ParticleTypes.CLOUD,
                    x + (RANDOM.nextDouble() - 0.5) * 1.5,
                    y + 0.5 + (RANDOM.nextDouble() - 0.5),
                    z + (RANDOM.nextDouble() - 0.5) * 1.5,
                    0, 0.01, 0);
        }
        if (RANDOM.nextInt(3) == 0) {
            // 体からハラハラと落ちる雪の結晶
            this.level().addParticle(ParticleTypes.SNOWFLAKE,
                    x + (RANDOM.nextDouble() - 0.5) * 1.2,
                    y + 1.5,
                    z + (RANDOM.nextDouble() - 0.5) * 1.2,
                    0, -0.02, 0);
        }
        // =================================================================

        // 💡 以下は、先ほど実装した「アクションごとの追加エフェクト」
        int stateOrdinal = this.entityData.get(CURRENT_ACTION_STATE);
        PatternActionType state = PatternActionType.values()[stateOrdinal];

        switch (state) {
            case WAIT -> {
                // 待機中：さらに追加で足元に煙
                if (RANDOM.nextInt(3) == 0) {
                    this.level().addParticle(ParticleTypes.CLOUD, x + (RANDOM.nextDouble() - 0.5), y, z + (RANDOM.nextDouble() - 0.5), 0, 0.02, 0);
                }
            }
            case MOVE -> {
                // 移動中：飛行の軌跡をさらに強調
                this.level().addParticle(ParticleTypes.SNOWFLAKE, x, y + 0.5, z, 0, 0, 0);
            }
            case DOWN -> {
                // 急降下中：激しい冷気の渦
                for (int i = 0; i < 3; i++) {
                    this.level().addParticle(ParticleTypes.SNOWFLAKE, x + (RANDOM.nextDouble() - 0.5), y + 1.0, z + (RANDOM.nextDouble() - 0.5), 0, -0.5, 0);
                    this.level().addParticle(ParticleTypes.POOF, x, y + 1.5, z, 0, -0.2, 0);
                }
            }
            case CAST_SPELL -> {
                // 魔法詠唱中：ボスの周りを冷気の魔法陣のようにパーティクルが回転
                for (int i = 0; i < 5; i++) {
                    double angle = RANDOM.nextDouble() * 2 * Math.PI;
                    double radius = 1.2;
                    double px = x + Math.cos(angle) * radius;
                    double pz = z + Math.sin(angle) * radius;
                    this.level().addParticle(ParticleTypes.SOUL, px, y, pz, 0, 0.1, 0);
                }
            }
        }
    }

    // --- パターン登録エリア ---
    private void initPatterns() {
        this.allPatterns.clear();

        registerPattern(new ActionPattern("ice_spike_min", 1, 0.0, 3.0,
                PatternAction.down(60),
                PatternAction.spell(SpellRegistry.ICE_SPIKES_SPELL.get(), 30),
                PatternAction.wait(20),
                PatternAction.spell(SpellRegistry.FROSTWAVE_SPELL.get(), 50),
                PatternAction.move(-20),
                PatternAction.wait(5) // 魔法を撃ったあと2秒間(40Tick)のクールダウン
        ));

        registerPattern(new ActionPattern("snowy_area", 1, 3.0, 12.0,
                PatternAction.move(10),
                PatternAction.spell(SpellRegistry.FROSTWAVE_SPELL.get(), 10),
                PatternAction.move(-30),
                PatternAction.spell(SpellRegistry.ICE_SPIKES_SPELL.get(), 25),
                PatternAction.wait(5),
                PatternAction.spell(SpellRegistry.ICE_SPIKES_SPELL.get(), 25),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 50),
                PatternAction.wait(5) // 魔法を撃ったあと2秒間(40Tick)のクールダウン
        ));

        registerPattern(new ActionPattern("not_ice_spike_min", 1, 0.0, 3.0,
                PatternAction.down(60),
                PatternAction.spell(SpellRegistry.RAY_OF_FROST_SPELL.get(), 30),
                PatternAction.spell(SpellRegistry.RAY_OF_FROST_SPELL.get(), 30),
                PatternAction.wait(10),
                PatternAction.spell(SpellRegistry.RAY_OF_FROST_SPELL.get(), 30),
                PatternAction.spell(SpellRegistry.RAY_OF_FROST_SPELL.get(), 30),
                PatternAction.move(-20),
                PatternAction.wait(5) // 魔法を撃ったあと2秒間(40Tick)のクールダウン
        ));

        registerPattern(new ActionPattern("not_snowy_area", 1, 3.0, 12.0,
                PatternAction.move(10),
                PatternAction.spell(SpellRegistry.FROSTWAVE_SPELL.get(), 10),
                PatternAction.wait(30),
                PatternAction.spell(Modspellregistry.SWING_SNOW_BALL.get(), 25),
                PatternAction.wait(5),
                PatternAction.spell(Modspellregistry.ICE_CAGE.get(), 20),
                PatternAction.spell(SpellRegistry.ICE_TOMB_SPELL.get(), 20),
                PatternAction.wait(20),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 50),
                PatternAction.spell(SpellRegistry.SUMMON_POLAR_BEAR_SPELL.get(), 30),
                PatternAction.wait(20),// 魔法を撃ったあと2秒間(40Tick)のクールダウン
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 25)
        ));

        // 🏹 中距離（6.0 〜 12.0マス）：30Tick(1.5秒)溜めてからアイススパイク
        registerPattern(new ActionPattern("ice_spike", 1, 12.0, 15.0,
                PatternAction.wait(15),
                PatternAction.spell(SpellRegistry.ICE_SPIKES_SPELL.get(), 5),
                PatternAction.spell(SpellRegistry.RAY_OF_FROST_SPELL.get(), 1),
                PatternAction.wait(5),
                PatternAction.spell(SpellRegistry.RAY_OF_FROST_SPELL.get(), 1),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 50),
                PatternAction.wait(20)
        ));

        registerPattern(new ActionPattern("flost_dash", 1, 8.0, 26.0, // Float.MAX_VALUEだと遠すぎてバグる事があるので交戦距離レンジに
                PatternAction.down(10),
                PatternAction.spell(Modspellregistry.FROST_DASH.get(), 12),
                PatternAction.wait(5),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 30),
                PatternAction.down(10),
                PatternAction.spell(Modspellregistry.FROST_DASH.get(), 12),
                PatternAction.wait(5),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 30)
        ));

        registerPattern(new ActionPattern("snow_pan", 1, 26.0, 34.0, // Float.MAX_VALUEだと遠すぎてバグる事があるので交戦距離レンジに
                PatternAction.wait(5),
                PatternAction.spell(SpellRegistry.ICICLE_SPELL.get(), 15),
                PatternAction.spell(SpellRegistry.ICICLE_SPELL.get(), 15),
                PatternAction.spell(SpellRegistry.ICICLE_SPELL.get(), 15),
                PatternAction.spell(Modspellregistry.SNOWFLAKE.get(), 15),
                PatternAction.wait(60),
                PatternAction.spell(Modspellregistry.FALL_SNOWBALL.get(), 5),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 50),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 50),
                PatternAction.wait(5)
        ));

        registerPattern(new ActionPattern("not_snow_pan", 1, 26.0, 34.0, // Float.MAX_VALUEだと遠すぎてバグる事があるので交戦距離レンジに
                PatternAction.wait(5),
                PatternAction.spell(SpellRegistry.ICICLE_SPELL.get(), 15),
                PatternAction.spell(SpellRegistry.ICICLE_SPELL.get(), 15),
                PatternAction.spell(SpellRegistry.ICICLE_SPELL.get(), 15),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 50),
                PatternAction.wait(10),
                PatternAction.spell(Modspellregistry.SNOWFLAKE.get(), 15),
                PatternAction.spell(Modspellregistry.ICE_CAGE.get(), 20),
                PatternAction.wait(20),
                PatternAction.spell(SpellRegistry.ICE_TOMB_SPELL.get(), 20),
                PatternAction.spell(Modspellregistry.SWING_SNOW_BALL.get(), 15),
                PatternAction.wait(5)
        ));

        registerPattern(new ActionPattern("snow_pans", 1, 34.0, 49.0, // Float.MAX_VALUEだと遠すぎてバグる事があるので交戦距離レンジに
                PatternAction.spell(Modspellregistry.SWING_SNOW_BALL.get(), 15),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 10),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 10),
                PatternAction.wait(5)
        ));

        registerPattern(new ActionPattern("snow_pa", 1, 49.0, 62.0, // Float.MAX_VALUEだと遠すぎてバグる事があるので交戦距離レンジに
                PatternAction.wait(5),
                PatternAction.move(40),
                PatternAction.spell(SpellRegistry.RAY_OF_FROST_SPELL.get(), 1),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 50),
                PatternAction.wait(30)
        ));

        registerPattern(new ActionPattern("ice_spike_min", 1, 32.0, 64.0,
                PatternAction.wait(60),
                PatternAction.spell(SpellRegistry.SUMMON_POLAR_BEAR_SPELL.get(), 30),
                PatternAction.spell(SpellRegistry.SUMMON_POLAR_BEAR_SPELL.get(), 30),
                PatternAction.spell(SpellRegistry.SUMMON_POLAR_BEAR_SPELL.get(), 30),
                PatternAction.spell(SpellRegistry.SUMMON_POLAR_BEAR_SPELL.get(), 30),
                PatternAction.move(60),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 10),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 10),
                PatternAction.spell(SpellRegistry.FROST_STEP_SPELL.get(), 10),
                PatternAction.wait(40) // 魔法を撃ったあと2秒間(40Tick)のクールダウン
        ));
    }

    @Override
    protected void registerGoals() {
        // 1. 基本行動（水泳・視線・徘徊）
        this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(this));
        this.goalSelector.addGoal(6, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F));
        this.goalSelector.addGoal(7, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));

        // 2. ⚡これがないと getTarget() が常に null になります！
        this.targetSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.player.Player.class, true));
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.npc.AbstractVillager.class, false));
    }

    private void registerPattern(ActionPattern pattern) {
        this.allPatterns.add(pattern);
        this.patternMap.put(pattern.name(), pattern);
    }
}