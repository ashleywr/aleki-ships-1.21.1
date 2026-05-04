package com.alekiponi.alekiships.mixins.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Final
    @Shadow
    private Entity entity;
    @Shadow
    private List<Entity> lastPassengers;
    @Final
    @Shadow
    private Consumer<Packet<?>> broadcast;

    @Shadow
    private static Stream<Entity> removedPassengers(List<Entity> pInitialPassengers, List<Entity> pCurrentPassengers) {
        return Stream.empty();
    }

    @Inject(method = "sendChanges", at = @At(value = "HEAD"), cancellable = true)
    private void alekiships$waitToSendChanges(CallbackInfo ci) {
        if (!(this.entity instanceof AbstractVehicle vehicle)) return;
        if (vehicle.hasAllParts() || !vehicle.isFunctional()) return;

        ci.cancel();
    }

    @Inject(method = "sendChanges", at = @At(value = "HEAD"))
    private void alekiships$updatePassengersForFlaggedVehicles(CallbackInfo ci) {
        if (!(this.entity instanceof AbstractVehicle vehicle)) return;

        if (!vehicle.isFlaggedForPassengerUpdate() || !vehicle.isFunctional()) return;

        final var passengers = vehicle.getPassengers();
        this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
        removedPassengers(passengers, this.lastPassengers).filter(player -> player instanceof ServerPlayer)
                .map(player -> (ServerPlayer) player)
                .forEach(serverPlayer -> serverPlayer.connection.teleport(serverPlayer.getX(), serverPlayer.getY(),
                        serverPlayer.getZ(), serverPlayer.getYRot(), serverPlayer.getXRot()));
        this.lastPassengers = passengers;
        vehicle.setFlaggedForPassengerUpdate(false);
    }
}
