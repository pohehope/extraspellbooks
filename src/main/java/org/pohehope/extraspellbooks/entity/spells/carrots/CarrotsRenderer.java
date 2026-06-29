package org.pohehope.extraspellbooks.entity.spells.carrots;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.pohehope.extraspellbooks.Extraspellbooks;
import org.pohehope.extraspellbooks.entity.spells.cucumber.Cucumber;
import software.bernie.geckolib.model.GeoModel;

public class CarrotsRenderer extends EntityRenderer<Carrots> {

    public static final ResourceLocation[] TEXTURES = {
            Extraspellbooks.id("textures/entity/carrot/carrot.png")
    };

    public CarrotsRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }


    @Override
    public void render(Carrots entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        renderModel(poseStack, bufferSource, 0);
        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public static void renderModel(PoseStack poseStack, MultiBufferSource bufferSource, int animOffset) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        // テクスチャのバッファを取得（エネルギーエフェクト風ではなく、通常のカットアウト＝透過対応にするのがおすすめ）
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(animOffset)));

        // 人参のサイズ（幅と高さ）
        float halfWidth = 2.5f;  // マイクラの1ブロックの半分くらい
        float height = 5.0f;     // 高さを1ブロック分に

        // ======= 1枚目のパネル (Y軸を中心に45度回転) =======
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(45));

        // Y軸が上方向なので、高さ(height)をYのプラス方向に設定して板を貼る
        consumer.vertex(poseMatrix, -halfWidth, 0, 0).color(255, 255, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 0f, 1f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0, 0).color(255, 255, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 0f, 1f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, height, 0).color(255, 255, 255, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 0f, 1f).endVertex();
        consumer.vertex(poseMatrix, -halfWidth, height, 0).color(255, 255, 255, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 0f, 1f).endVertex();
        poseStack.popPose();

        // ======= 2枚目のパネル (Y軸を中心に-45度回転させて交差させる) =======
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-45));

        consumer.vertex(poseMatrix, -halfWidth, 0, 0).color(255, 255, 255, 255).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 0f, 1f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0, 0).color(255, 255, 255, 255).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 0f, 1f).endVertex();
        consumer.vertex(poseMatrix, halfWidth, height, 0).color(255, 255, 255, 255).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 0f, 1f).endVertex();
        consumer.vertex(poseMatrix, -halfWidth, height, 0).color(255, 255, 255, 255).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(normalMatrix, 0f, 0f, 1f).endVertex();
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Carrots entity) {
        return TEXTURES[0];    }
    public static ResourceLocation getTextureLocation(int offset) {
        float ticksPerFrame = 1f;
        return TEXTURES[(int) (offset / ticksPerFrame) % TEXTURES.length];
    }
}
