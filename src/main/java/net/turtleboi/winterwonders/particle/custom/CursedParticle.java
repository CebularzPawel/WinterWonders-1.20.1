package net.turtleboi.winterwonders.particle.custom;

import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CursedParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    public CursedParticle(ClientLevel level, double x, double y, double z,
                          double xSpeed, double ySpeed, double zSpeed,
                          SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.friction = 0.48F;
        this.gravity = -0.1F;
        this.sprites = sprites;
        this.quadSize *= 0.75F;
        this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            CursedParticle particle = new CursedParticle(level, x, y, z, 0.0, 0.0, 0.0, this.spriteSet);

            float red = (float) xSpeed;
            float green = (float) ySpeed;
            float blue = (float) zSpeed;
            particle.setColor(red, green, blue);

            return particle;
        }
    }
}
