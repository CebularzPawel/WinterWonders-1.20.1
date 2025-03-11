package net.cebularz.winterwonders.client.renderer.util;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class RepeatingVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float uScale;
    private final float vScale;

    public RepeatingVertexConsumer(VertexConsumer delegate, float uScale, float vScale) {
        this.delegate = delegate;
        this.uScale = uScale;
        this.vScale = vScale;
    }

    @Override
    public VertexConsumer vertex(double pX, double pY, double pZ) {
        delegate.vertex(pX, pY, pZ);
        return this;
    }

    @Override
    public VertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha) {
        delegate.color(pRed, pGreen, pBlue, pAlpha);
        return this;
    }

    @Override
    public VertexConsumer uv(float pU, float pV) {
        delegate.uv(pU * uScale, pV * vScale);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int pU, int pV) {
        delegate.overlayCoords(pU, pV);
        return this;
    }

    @Override
    public VertexConsumer uv2(int pU, int pV) {
        delegate.uv2(pU, pV);
        return this;
    }

    @Override
    public VertexConsumer normal(float pX, float pY, float pZ) {
        delegate.normal(pX, pY, pZ);
        return this;
    }

    @Override
    public void endVertex() {

    }

    @Override
    public void defaultColor(int pDefaultR, int pDefaultG, int pDefaultB, int pDefaultA) {

    }

    @Override
    public void unsetDefaultColor() {

    }
}
