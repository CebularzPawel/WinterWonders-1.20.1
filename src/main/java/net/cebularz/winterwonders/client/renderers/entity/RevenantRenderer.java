package net.cebularz.winterwonders.client.renderers.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import net.cebularz.winterwonders.WinterWonders;

public class RevenantRenderer extends ZombieRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/revenant.png");

    public RevenantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new RevenantEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie pEntity) {
        return TEXTURE;
    }
}
