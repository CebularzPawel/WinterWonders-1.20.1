package net.cebularz.winterwonders.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import net.turtleboi.turtlecore.network.CoreNetworking;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Fix sendToNear in TurtleCore to use the server view distance, then remove this mixin... May already be fixed in 3.0.0
//Just made this so that the game wouldn't crash cos past me is cheeks at coding
@Mixin(value = CoreNetworking.class, remap = false)
public abstract class CoreNetworkingMixin {
    @Shadow
    private static SimpleChannel INSTANCE;

    @Inject(method = "sendToNear", at = @At("HEAD"), cancellable = true)
    private static void winterwonders$serverViewDistance(Object message, LivingEntity entity, CallbackInfo callback) {
        if (entity.level() instanceof ServerLevel level) {
            double range = level.getServer().getPlayerList().getViewDistance() * 16D;
            INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                    entity.getX(), entity.getY(), entity.getZ(), range, level.dimension())), message);
        }
        callback.cancel();
    }
}
