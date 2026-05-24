package com.alekiponi.alekiships.client;

import com.alekiponi.alekiships.client.model.DynamicTextureModel;
import com.alekiponi.alekiships.client.particle.ConstructionParticle;
import com.alekiponi.alekiships.client.render.icon.IconRenderDispatcher;
import com.alekiponi.alekiships.common.particles.AlekiShipsParticles;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class AlekiShipsClientEvents {

    public static void init(final IEventBus modBus) {
        modBus.addListener(IngameOverlays::registerOverlays);
        modBus.addListener(AlekiShipsClientEvents::onRegisterReloadListeners);
        modBus.addListener(AlekiShipsClientEvents::registerModelLoaders);
        modBus.addListener(AlekiShipsClientEvents::registerParticleProviders);
        modBus.register(RenderEventHandler.class);
    }

    private static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(IconRenderDispatcher.INSTANCE);
    }

    private static void registerModelLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register(DynamicTextureModel.LOADER_ID, DynamicTextureModel.LOADER);
    }

    private static void registerParticleProviders(final RegisterParticleProvidersEvent event) {
        event.registerSpecial(AlekiShipsParticles.CONSTRUCTION.get(), ConstructionParticle::create);
    }
}