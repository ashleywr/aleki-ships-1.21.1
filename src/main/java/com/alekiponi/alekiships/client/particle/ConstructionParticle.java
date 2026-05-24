package com.alekiponi.alekiships.client.particle;

import com.alekiponi.alekiships.common.particles.ConstructionParticleOption;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.inventory.InventoryMenu;

public class ConstructionParticle extends TextureSheetParticle {

    private final float uo;
    private final float vo;

    public ConstructionParticle(final ClientLevel level, final double x, final double y, final double z,
            final double xSpeed, final double ySpeed, final double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.gravity = 1.0F;
        this.rCol = 0.6F;
        this.gCol = 0.6F;
        this.bCol = 0.6F;


        this.quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    public static ConstructionParticle create(final ConstructionParticleOption type, final ClientLevel level,
            final double x, final double y, final double z, final double xSpeed, final double ySpeed,
            final double zSpeed) {
        final var constructionParticle = new ConstructionParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
        final var blocksAtlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        constructionParticle.setSprite(blocksAtlas.apply(type.getTexture()));
        return constructionParticle;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1) / 4);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1) / 4);
    }
}