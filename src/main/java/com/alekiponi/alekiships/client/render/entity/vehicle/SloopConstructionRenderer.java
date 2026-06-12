package com.alekiponi.alekiships.client.render.entity.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import com.alekiponi.alekiships.client.model.entity.AnchorEntityModel;
import com.alekiponi.alekiships.client.model.entity.SloopConstructionModel;
import com.alekiponi.alekiships.client.model.entity.SloopEntityModel;
import com.alekiponi.alekiships.client.render.entity.vehicle.vehiclehelper.AnchorRenderer;
import com.alekiponi.alekiships.common.block.AlekiShipsBlocks;
import com.alekiponi.alekiships.common.entity.vehicle.SloopUnderConstructionEntity;
import com.alekiponi.alekiships.common.recipe.MultiblockPattern;
import com.alekiponi.alekiships.common.recipe.ingredient.block.BlockStateIngredient;
import com.alekiponi.alekiships.common.recipe.util.Pattern3D;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.StairsShape;

public class SloopConstructionRenderer extends UnderConstructionEntityRenderer<SloopUnderConstructionEntity> {

    private static final MultiblockPattern SCAFFOLDING;

    static {
        final var flatFrame = BlockStateIngredient.builder(AlekiShipsBlocks.BOAT_FRAME_FLAT);
        final var angled1 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.WEST,
                StairsShape.INNER_RIGHT, b -> b);
        final var angled2 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.WEST,
                StairsShape.INNER_LEFT, b -> b);
        final var angled3 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.NORTH,
                StairsShape.STRAIGHT, b -> b);
        final var angled4 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.SOUTH,
                StairsShape.STRAIGHT, b -> b);
        final var angled5 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.WEST,
                StairsShape.OUTER_RIGHT, b -> b);
        final var angled6 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.WEST,
                StairsShape.OUTER_LEFT, b -> b);
        final var angled7 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.EAST,
                StairsShape.INNER_LEFT, b -> b);
        final var angled8 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.EAST,
                StairsShape.STRAIGHT, b -> b);
        final var angled9 = BlockStateIngredient.stairs(AlekiShipsBlocks.BOAT_FRAME_ANGLED.get(), Direction.EAST,
                StairsShape.INNER_RIGHT, b -> b);
        SCAFFOLDING = MultiblockPattern.builder()
                .layers(Pattern3D.Layer.builder()
                        .row(" 12 ")
                        .row(" 34 ")
                        .row("1562")
                        .row("3FF4")
                        .row("3FF4")
                        .row("3FF4")
                        .row("7889")
                        .build())
                .define('F', flatFrame)
                .define('1', angled1)
                .define('2', angled2)
                .define('3', angled3)
                .define('4', angled4)
                .define('5', angled5)
                .define('6', angled6)
                .define('7', angled7)
                .define('8', angled8)
                .define('9', angled9)
                .build();
    }

    protected final SloopConstructionModel sloopModel;
    protected final AnchorEntityModel anchorModel;

    public SloopConstructionRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;
        this.sloopModel = new SloopConstructionModel(context.bakeLayer(SloopEntityModel.LAYER_LOCATION));
        this.anchorModel = new AnchorEntityModel(context.bakeLayer(AnchorEntityModel.LAYER_LOCATION));
    }

    @Override
    public void render(final SloopUnderConstructionEntity constructionSloop, final float entityYaw,
            final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource,
            final int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));

        poseStack.translate(0, 1.0625F, 0);
        poseStack.scale(-1, -1, 1);

        poseStack.translate(0, 1.57F, 0);

        final var vertexConsumer = bufferSource.getBuffer(
                this.sloopModel.renderType(this.getTextureLocation(constructionSloop)));
        this.sloopModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.DECK.ordinal()) {
            //render keel
            this.sloopModel.getKeel().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.BOWSPRIT.ordinal()) {
            //render deck
            for (final var part : sloopModel.getDeck()) {
                part.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.MAST.ordinal()) {
            //render bowsprit
            this.sloopModel.getBowsprit().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.BOOM.ordinal()) {
            //render mast
            this.sloopModel.getMast().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.MAINSAIL.ordinal()) {
            //render boom and gaff
            /*
            for(ModelPart part : sloopModel.getBoomGaff()){
            }*/
            sloopModel.getBoomGaff()[0].render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.JIBSAIl.ordinal()) {
            //render mainsail furled
            this.sloopModel.getMainsail().render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.RAILINGS_STERN.ordinal()) {
            //render jibsail furled
            poseStack.pushPose();
            poseStack.translate(0, -1.57F, 0);
            this.sloopModel.getJibsailFurled()
                    .render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.RAILINGS_BOW.ordinal()) {
            //render stern railing
            for (ModelPart part : sloopModel.getSternRailing()) {
                part.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.ANCHOR.ordinal()) {
            //render bow railing
            for (final var part : sloopModel.getBowRailing()) {
                part.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        }
        if (constructionSloop.getConstructionState()
                    .inputStage()
                    .ordinal() >= SloopUnderConstructionEntity.SloopConstructionStage.RIGGING.ordinal()) {
            poseStack.pushPose();

            poseStack.translate(1.34f + 1.3, -2.52 + 1.6, -2.52 - 0.75);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(60));

            final var vertexConsumerAnchor = bufferSource.getBuffer(this.anchorModel.renderType(AnchorRenderer.ANCHOR));
            this.anchorModel.renderToBuffer(poseStack, vertexConsumerAnchor, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        //render scaffolding blocks
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(270));
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.translate(-4, 0.08, -2);
        this.renderMultiblockPattern(SCAFFOLDING, poseStack, bufferSource, packedLight);
        poseStack.popPose();

        poseStack.popPose();
        super.render(constructionSloop, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final SloopUnderConstructionEntity constructionEntity) {
        return constructionEntity.getTexture();
    }
}