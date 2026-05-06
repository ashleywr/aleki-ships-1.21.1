package com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractAlekiBoatEntity;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import com.alekiponi.alekiships.common.entity.vehiclehelper.MastEntity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

public class MastRenderer extends EntityRenderer<MastEntity> {
    private final ModelPart flag;
    private final ModelPart bar;

    public MastRenderer(final EntityRendererProvider.Context context) {
        super(context);
        final var root = context.bakeLayer(ModelLayers.BANNER);
        this.flag = root.getChild(BannerRenderer.FLAG);
        this.bar = root.getChild(BannerRenderer.BAR);
    }

    @Override
    public void render(final MastEntity mast, final float entityYaw, final float partialTicks,
            final PoseStack poseStack, final MultiBufferSource bufferSource, int packedLight) {
        AbstractVehicle vehicle = mast.getTrueVehicle();
        if (vehicle != null && LightTexture.block(packedLight) < vehicle.getCompartmentBlockLight()) {
            packedLight = LightTexture.pack(Math.max(0, vehicle.getCompartmentBlockLight() - 1),
                    getSkyLightLevel(mast, mast.blockPosition()));
        }

        if (!(mast.getTrueVehicle() instanceof AbstractAlekiBoatEntity<?> ship)) return;

        final float rotation = ship.getWindLocalRotation() + ship.getYRot();
        final float height = mast.getBbHeight() + 0.4175f;

        poseStack.pushPose();

        poseStack.translate(0, height, 0);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - ship.getYRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        poseStack.scale(0.6766667F, -0.6666667F, -0.6666667F);
        poseStack.translate(0, 1.935f, 0);
        VertexConsumer vertexconsumer = ModelBakery.BANNER_BASE.buffer(bufferSource, RenderType::entitySolid);
        this.bar.render(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        // render banner itself
        float windLocalAngle = Mth.wrapDegrees(ship.getWindLocalRotation() - ship.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        poseStack.translate(0, 0, -0.05f);

        final var banner = mast.getBanner();
        if (banner.isPresent()) {
            renderBanner(banner.get(), poseStack, bufferSource, packedLight);
        }
        poseStack.popPose();

        super.render(mast, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderBanner(final MastEntity.MastBanner banner, final PoseStack poseStack,
            final MultiBufferSource bufferSource, final int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        BannerRenderer.renderPatterns(poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, this.flag,
                ModelBakery.BANNER_BASE, true, banner.baseColor(), banner.patternLayers());
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(final MastEntity mastEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}