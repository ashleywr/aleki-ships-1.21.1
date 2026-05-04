package com.alekiponi.alekiships.mixins.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.alekiponi.alekiships.common.entity.compartment.AbstractCompartmentEntity;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> {

    @Shadow
    @Final
    public ModelPart rightLeg;

    @Shadow
    @Final
    public ModelPart leftLeg;

    @Definition(id = "riding", field = "Lnet/minecraft/client/model/HumanoidModel;riding:Z")
    @Expression("this.riding")
    @ModifyExpressionValue(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean alekiships$applyRidingPose(final boolean original, @Local(argsOnly = true) final T entity) {
        if (!(entity.getVehicle() instanceof AbstractCompartmentEntity compartment)) return original;

        return switch (compartment.getRidingPose()) {
            case STANDING -> {
                this.rightLeg.setPos(-1.9F, 12, 0);
                this.leftLeg.setPos(01.9F, 12, 0);

                this.rightLeg.setRotation(0, 0, 0);
                this.leftLeg.setRotation(0, 0, 0);
                yield false;
            }
            case COMPACT -> {
                this.rightLeg.setRotation(-1.570796F, 0, 0);
                this.leftLeg.setRotation(-1.570796F, -0.002F, -0.002F);

                this.rightLeg.setPos(-1.9F, 11.5F, 0.0F);
                this.leftLeg.setPos(01.9F, 11.5F, 0.0F);
                yield false;
            }
            case ULTRA_COMPACT -> {
                this.rightLeg.setRotation(-1.570796F, -0.1570796F, 0);
                this.leftLeg.setRotation(-1.570796F, 0.1570796F, 0);

                this.rightLeg.setPos(-1.9F, 13.6F, 1F);
                this.leftLeg.setPos(01.9F, 13.6F, 1F);
                yield false;
            }
            case STANDARD -> original;
        };
    }
}
