package com.alekiponi.alekiships.client;

import com.mojang.blaze3d.vertex.PoseStack;

import com.alekiponi.alekiships.AlekiShips;
import com.alekiponi.alekiships.client.event.IconRenderersEvent;
import com.alekiponi.alekiships.client.render.icon.IconRenderDispatcher;
import com.alekiponi.alekiships.common.entity.compartment.EmptyCompartmentEntity;
import com.alekiponi.alekiships.common.entity.vehicle.SloopEntity;
import com.alekiponi.alekiships.config.AlekiShipsConfig;
import com.alekiponi.alekiships.util.CommonHelper;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.TranslatableEnum;

import java.awt.*;
import java.text.DecimalFormat;

public final class IngameOverlays {
    public static final ResourceLocation ENTITY_ICON = AlekiShips.location("entity_icon");
    public static final ResourceLocation PASSENGER_STATUS = AlekiShips.location("passenger_status");
    public static final ResourceLocation SAILING_ELEMENT = AlekiShips.location("sailing_element");

    public static final ResourceLocation SAILING_ICONS = AlekiShips.location("textures/gui/icons/sailing_icons.png");
    public static final ResourceLocation SPEEDOMETER_ICONS = AlekiShips.location(
            "textures/gui/icons/speedometer_icons.png");

    public static final String EJECT_PASSENGERS_KEY = AlekiShips.lang("overlay.eject_passengers");
    public static final String DEBUG_KILOMETERS_PER_HOUR_KEY = AlekiShips.lang("debug", "boatSpeedKilometersPerHour");
    public static final String DEBUG_MILES_PER_HOUR_KEY = AlekiShips.lang("debug", "boatSpeedMilesPerHour");

    public static void registerOverlays(final RegisterGuiLayersEvent event) {
        ModLoader.postEvent(new IconRenderersEvent.RegisterIconRenderersEvent());
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, ENTITY_ICON, IngameOverlays::renderEntityIcon);
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, PASSENGER_STATUS, IngameOverlays::renderPassengerStatus);
        event.registerAbove(VanillaGuiLayers.HOTBAR, SAILING_ELEMENT, IngameOverlays::renderSailingElement);
    }

    private static void renderEntityIcon(final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) return;

        final var player = minecraft.player;

        if (!(minecraft.getCameraEntity() instanceof Player) || !minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }

        final Entity entity = CommonHelper.getEntity(minecraft.hitResult);

        if (entity != null) {
            IconRenderDispatcher.INSTANCE.render(entity, player, guiGraphics,
                    deltaTracker.getGameTimeDeltaPartialTick(false));
        }
    }

    private static void renderPassengerStatus(final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;

        if (minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR || !minecraft.options.getCameraType()
                .isFirstPerson()) {
            return;
        }

        final Entity entity = CommonHelper.getEntity(minecraft.hitResult);

        if (entity == null) return;

        final PoseStack stack = guiGraphics.pose();

        // Targeted entity isn't riding an Empty Compartment
        if (!(entity.getVehicle() instanceof EmptyCompartmentEntity)) {
            // Targeted entity isn't an empty compartment with a rider
            if (!(entity instanceof EmptyCompartmentEntity compartment) || !compartment.hasPassenger(e -> true)) {
                return;
            }
        }

        // The passenger of the compartment isn't living
        if ((!(entity instanceof EmptyCompartmentEntity) && !(entity instanceof LivingEntity)) || (entity instanceof ArmorStand)) {
            return;
        }
        // The passenger isn't a stupid gd armor stand
        if (entity instanceof EmptyCompartmentEntity compartment && compartment.getFirstPassenger() instanceof ArmorStand) {
            return;
        }

        stack.pushPose();

        stack.translate(guiGraphics.guiWidth() / 2F, guiGraphics.guiHeight() / 2F - 15, 0);
        stack.scale(1, 1, 1);

        // Should look like: Press Left Shift + Right Button to eject
        final String string = Component.translatable(EJECT_PASSENGERS_KEY,
                minecraft.options.keyShift.getTranslatedKeyMessage(),
                minecraft.options.keyUse.getTranslatedKeyMessage()).getString();

        guiGraphics.drawString(minecraft.font, string, -minecraft.font.width(string) / 2, 0, Color.WHITE.getRGB(),
                true);

        stack.popPose();
    }

    private static void renderSailingElement(final GuiGraphics guiGraphics, final DeltaTracker deltaTracker) {
        final Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) return;
        if (minecraft.gameMode == null) return;

        if (minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) return;

        if (!(minecraft.player.getRootVehicle() instanceof SloopEntity sloopEntity)) return;

        if (sloopEntity.getPilotCompartment() == null) return;
        if (!sloopEntity.getPilotCompartment().hasExactlyOnePlayerPassenger()) return;
        if (sloopEntity.getPilotCompartment().getFirstPassenger() != minecraft.player) return;

        final PoseStack stack = guiGraphics.pose();
        stack.pushPose();

        stack.scale(1, 1, 1);

        final int x = guiGraphics.guiWidth() / 2;
        final int y = guiGraphics.guiHeight() - minecraft.gui.rightHeight;

        stack.translate(x + 1, y + 4, 0);
        if (guiGraphics.guiHeight() % 2 != 0) {
            stack.translate(0, 0.5F, 0);
        }
        if (guiGraphics.guiWidth() % 2 != 0) {
            stack.translate(0.5f, 0, 0);
        }


        final int frameIndex;
        {
            final int windSpeed = Mth.clamp((int) (sloopEntity.getLocalWindSpeed() * 160), 1, 20);
            final int ticksBetweenFrames = Mth.clamp(Math.abs(windSpeed - 20), 1, 20);
            final int ticks = sloopEntity.tickCount / ticksBetweenFrames;
            frameIndex = ticks % 32;
        }

        final int offhandOffset = !minecraft.player.getOffhandItem().isEmpty() ? 26 : 3;
        final double smoothSpeedMS = sloopEntity.getSmoothSpeedMS();

        if (minecraft.gui.getDebugOverlay().showDebugScreen()) {
            final var decimalFormat = new DecimalFormat("0.#");
            final var speed = switch (AlekiShipsConfig.CLIENT.debugDisplayUnits.get()) {
                case KILOMETRES_PER_HOUR -> {
                    final var kilometersPerHour = decimalFormat.format(smoothSpeedMS * 3.6);
                    yield Component.translatable(DEBUG_KILOMETERS_PER_HOUR_KEY, kilometersPerHour);
                }
                case MILES_PER_HOUR -> {
                    final var milesPerHour = decimalFormat.format(smoothSpeedMS * 2.236936);
                    yield Component.translatable(DEBUG_MILES_PER_HOUR_KEY, milesPerHour);
                }
            };

            guiGraphics.drawString(minecraft.font, speed, -134, -1 - offhandOffset, -1, true);
        }

        final int angle;
        {
            int deferredAngle = Math.round((Mth.wrapDegrees(sloopEntity.getWindLocalRotation()) / 360) * 64) + 32;
            if (deferredAngle == 64) {
                deferredAngle = 0;
            }
            angle = deferredAngle;
        }

        final int speedometerIndex = Mth.clamp((int) (smoothSpeedMS - 2) * 2, 0, 31);

        guiGraphics.blit(SAILING_ICONS, -126, 3 - offhandOffset, 32 * angle, 32 * (angle / 8), 32, 32);
        guiGraphics.blit(SPEEDOMETER_ICONS, -126 - 8, 3 - offhandOffset + 32 - 16, 16 * speedometerIndex,
                16 * (speedometerIndex / 16), 16, 16);
        guiGraphics.blit(SPEEDOMETER_ICONS, -126 - 8, 3 - offhandOffset, 16 * frameIndex, 32 + 16 * (frameIndex / 16),
                16, 16);
        stack.popPose();
    }

    public enum DebugDisplayUnits implements TranslatableEnum {
        KILOMETRES_PER_HOUR,
        MILES_PER_HOUR;

        @Override
        public Component getTranslatedName() {
            return CommonHelper.translateEnum(this);
        }
    }
}