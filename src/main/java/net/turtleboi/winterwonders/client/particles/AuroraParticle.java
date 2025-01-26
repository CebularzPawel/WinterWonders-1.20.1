package net.turtleboi.winterwonders.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AuroraParticle extends TextureSheetParticle {
    private final double initialY;
    private final float glowIntensity;
    public AuroraParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);

        this.initialY = y;
        this.lifetime = 100;
        this.gravity = 0;

        // Thank you mojank
        float r = Mth.randomBetween(random, 0.2f, 0.8f);
        float g = Mth.randomBetween(random, 0.2f, 0.8f);
        float b = Mth.randomBetween(random, 0.2f, 1.0f);

        this.setColor(r, g, b);
        this.alpha = 0.7f;
        this.glowIntensity = Mth.randomBetween(random, 0.7f, 1.2f);
        this.quadSize = 0.3F;
    }

    @Override
    public void tick() {

        float pulse = Mth.sin(this.age * 0.2f) * 0.1f;
        this.quadSize = 0.3f + pulse;

        super.tick();
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTicks) {
        Vector3f[] vertices = new Vector3f[4];
        Quaternionf rotation = camera.rotation();

        vertices[0] = new Vector3f(-1.0F, -1.0F, 0.0F);
        vertices[1] = new Vector3f(-1.0F, 1.0F, 0.0F);
        vertices[2] = new Vector3f(1.0F, 1.0F, 0.0F);
        vertices[3] = new Vector3f(1.0F, -1.0F, 0.0F);

        for (Vector3f vertex : vertices) {
            vertex.rotate(rotation);
            vertex.mul(this.quadSize);
        }

        float glowPulse = Mth.sin(this.age * 0.2f) * glowIntensity;
        float r = this.rCol * (1.0f + glowPulse);
        float g = this.gCol * (1.0f + glowPulse);
        float b = this.bCol * (1.0f + glowPulse);
        int light = 0xF000F0;
        //TODO::FIX THE VERTEX
//        consumer.vertex(this.x + vertices[0].x(), this.y + vertices[0].y(), this.z + vertices[0].z())
//                .color(r, g, b, this.alpha)
//                .uv(this.getU0(), this.getV0())
//                .uv2(light)
//                .endVertex();
//        consumer.vertex(this.x + vertices[1].x(), this.y + vertices[1].y(), this.z + vertices[1].z())
//                .color(r, g, b, this.alpha)
//                .uv(this.getU0(), this.getV1())
//                .uv2(light)
//                .endVertex();
//        consumer.vertex(this.x + vertices[2].x(), this.y + vertices[2].y(), this.z + vertices[2].z())
//                .color(r, g, b, this.alpha)
//                .uv(this.getU1(), this.getV1())
//                .uv2(light)
//                .endVertex();
//        consumer.vertex(this.x + vertices[3].x(), this.y + vertices[3].y(), this.z + vertices[3].z())
//                .color(r, g, b, this.alpha)
//                .uv(this.getU1(), this.getV0())
//                .uv2(light)
//                .endVertex();

    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            AuroraParticle particle = new AuroraParticle(level, x, y, z);
            particle.pickSprite(sprites);
            return particle;
        }
    }
}
