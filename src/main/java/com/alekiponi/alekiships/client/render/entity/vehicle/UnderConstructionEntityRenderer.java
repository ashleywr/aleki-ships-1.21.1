package com.alekiponi.alekiships.client.render.entity.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;

import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.ConstructionRenderer;
import com.alekiponi.alekiships.common.entity.vehicle.AbstractUnderConstructionEntity;
import com.alekiponi.alekiships.common.recipe.MultiblockPattern;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.neoforged.neoforge.client.model.data.ModelData;

public abstract class UnderConstructionEntityRenderer<E extends AbstractUnderConstructionEntity<?,?>> extends EntityRenderer<E> {
    private final BlockRenderDispatcher blockRenderer;
    private final ConstructionRenderer constructionRenderer;

    protected UnderConstructionEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.constructionRenderer = new ConstructionRenderer(context);
    }

    protected final void renderMultiblockPattern(final MultiblockPattern pattern, final PoseStack poseStack,
            final MultiBufferSource bufferSource, final int packedLight) {
        for (final var info : pattern.shape()) {
            poseStack.pushPose();
            final var position = info.position();
            poseStack.translate(position.getX(), position.getY(), position.getZ());
            final var matchingState = info.element().blockIngredient().getMatchingStates()[0].trySetValue(
                    BlockStateProperties.WATERLOGGED, false);
            this.blockRenderer.renderSingleBlock(matchingState, poseStack, bufferSource, packedLight,
                    OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
            poseStack.popPose();
        }
    }

    @Override
    public void render(final E underConstructionEntity, final float entityYaw, final float partialTick,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        this.renderConstructionEntity(underConstructionEntity, partialTick, poseStack, bufferSource, packedLight);
        super.render(underConstructionEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    private void renderConstructionEntity(final E underConstructionEntity, final float partialTick,
            final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        poseStack.pushPose();
        final var constructionEntityPosition = underConstructionEntity.getLocalConstructionEntityPosition();
        poseStack.translate(constructionEntityPosition.x, constructionEntityPosition.y, constructionEntityPosition.z);
        final var yaw = Mth.lerp(partialTick, underConstructionEntity.constructionEntity.yRotO,
                underConstructionEntity.constructionEntity.getYRot());
        this.constructionRenderer.render(underConstructionEntity.constructionEntity, yaw, partialTick, poseStack,
                bufferSource, packedLight);
        poseStack.popPose();
    }
}