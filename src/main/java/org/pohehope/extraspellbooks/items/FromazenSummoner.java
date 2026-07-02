package org.pohehope.extraspellbooks.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.boss.wither.WitherBoss; // 例としてウィザーを使用
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FromazenSummoner extends Item {
    public FromazenSummoner(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        // 処理はサーバー側のみで行う
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            BlockPos clickedPos = context.getClickedPos();
            BlockState clickedState = level.getBlockState(clickedPos);

            // 例：ダイヤモンドブロックの上で右クリックしたときのみ発動
            if (clickedState.is(Blocks.DIAMOND_BLOCK)) {
                Direction clickedFace = context.getClickedFace();
                BlockPos spawnPos = clickedPos.relative(clickedFace); // クリックした面の隣（上など）

                // --- ボスのスポーン処理 ---
                // ここでは例としてウィザー（EntityType.WITHER）を召喚
                // 自作のボスModEntityがある場合は、EntityType.YOUR_BOSS.create(...) に変更してください
                WitherBoss boss = EntityType.WITHER.create(serverLevel);

                if (boss != null) {
                    // スポーン位置と向きを設定
                    boss.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0.0F, 0.0F);

                    // スポーン時の初期化処理（ModのカスタムMobの場合は重要）
                    boss.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.SPAWN_EGG, null, null);

                    // 世界にエンティティを追加
                    serverLevel.addFreshEntity(boss);

                    // 周囲のプレイヤーにメッセージを表示（演出用）
                    if (context.getPlayer() != null) {
                        context.getPlayer().sendSystemMessage(Component.literal("ボスが目覚めた！"));
                    }

                    // アイテムを1つ減らす（クリエイティブモード以外）
                    ItemStack itemStack = context.getItemInHand();
                    if (!context.getPlayer().getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}
