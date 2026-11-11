package net.cebularz.winterwonders.events;

import com.mojang.blaze3d.vertex.PoseStack;
// import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
// import com.mojang.blaze3d.vertex.VertexConsumer;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.data.LichBossData;
import net.cebularz.winterwonders.client.gui.LichBossBar;
import net.cebularz.winterwonders.client.spell.SpellVisualManager;
// import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.item.custom.ThrowingSpearItem;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.ThrowingSpearAttackC2S;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
// import net.minecraft.client.resources.model.ModelBakery;
// import net.minecraft.core.BlockPos;
// import net.minecraft.server.level.BlockDestructionProgress;
// import net.minecraft.world.level.block.Blocks;
// import net.minecraft.world.level.block.state.BlockState;
// import net.minecraft.world.phys.AABB;
// import net.minecraft.world.phys.Vec3;
// import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (event.isAttack() && player != null && player.getMainHandItem().getItem() instanceof ThrowingSpearItem spear) {
            event.setCanceled(true);
            event.setSwingHand(false);
            if (spear.canJab(player)) {
                ModNetworking.sendToServer(new ThrowingSpearAttackC2S());
                player.swing(InteractionHand.MAIN_HAND);
                player.resetAttackStrengthTicker();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen == null) {
            LichBossData.removeFarAwayBosses();
            int x = event.getWindow().getGuiScaledWidth() / 2 - 96;
            int y = 10;
            LichBossBar.render(event.getGuiGraphics(), x, y, minecraft);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            Camera camera = event.getCamera();
            SpellVisualManager.renderIceSpikes(poseStack, buffer, camera);
        }
        // else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            // Minecraft minecraft = Minecraft.getInstance();
            // if (minecraft.level != null) {
                // PoseStack poseStack = event.getPoseStack();
                // Vec3 cameraPos = event.getCamera().getPosition();
                // MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().crumblingBufferSource();
                // for (BlockDestructionProgress damage : event.getLevelRenderer().destroyingBlocks.values()) {
                    // BlockPos pos = damage.getPos();
                    // BlockState state = minecraft.level.getBlockState(pos);
                    // if (minecraft.level.getEntity(damage.getId()) instanceof PinginEntity pingin && pingin.getWork() >= PinginEntity.CHIP
                            // && state.is(Blocks.ICE) && pos.distToCenterSqr(cameraPos) < 1024D
                            // && event.getFrustum().isVisible(new AABB(pos))) {
                        // poseStack.pushPose();
                        // poseStack.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
                        // PoseStack.Pose pose = poseStack.last();
                        // VertexConsumer cracks = new SheetedDecalTextureGenerator(buffer.getBuffer(ModelBakery.DESTROY_TYPES.get(damage.getProgress())),
                                // pose.pose(), pose.normal(), 1F);
                        // minecraft.getBlockRenderer().renderBreakingTexture(state, pos, minecraft.level,
                                // poseStack, cracks, ModelData.EMPTY);
                        // poseStack.popPose();
                    // }
                // }
                // buffer.endBatch();
            // }
        // }
    }
}
