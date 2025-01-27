package net.turtleboi.winterwonders.client.renderers.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.entity.custom.RevenantEntity;

public class RevenantRenderer extends ZombieRenderer {
    private static final ResourceLocation REVENANT_TEXTURE = new ResourceLocation(WinterWonders.MOD_ID, "textures/entity/revenant.png");

    public RevenantRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie pEntity) {
        return REVENANT_TEXTURE;
    }
}
