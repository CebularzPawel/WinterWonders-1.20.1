package net.cebularz.winterwonders.particle.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class ChilledParticles extends TextureSheetParticle {
    protected ChilledParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet,
                               double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, 0, -1, 0);

        this.hasPhysics = true;
        this.friction = 0.8f;
        float halfLife = lifetime * 0.5f;
        if (age < halfLife) {
            this.gravity = 0.1f;
        } else {
            float time = (age - halfLife) / halfLife;
            this.gravity = 0.1f + time * (0.9f);
        }
        this.lifetime = (int)(24.0D / (Math.random() * 0.8D + 0.2D));
        this.quadSize *= (float)((Math.random() * 0.55D + 0.35D));
        this.setSpriteFromAge(spriteSet);

        float rBase = 128 / 255f;
        float gBase = 233 / 255f;
        float bBase = 255 / 255f;

        float lighten = (float) Math.random();

        this.rCol = rBase + lighten * (1.0f - rBase);
        this.gCol = gBase + lighten * (1.0f - gBase);
        this.bCol = bBase + lighten * (1.0f - bBase);

    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut() {
        this.alpha = (-(1/(float)lifetime) * age + 1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
                                                 double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ChilledParticles(clientLevel, pX, pY, pZ, this.spriteSet, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}
