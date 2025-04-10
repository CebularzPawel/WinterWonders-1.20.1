package net.cebularz.winterwonders.effect.custom;

import net.cebularz.winterwonders.effect.ModEffects;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.FrozenDataS2C;
import net.cebularz.winterwonders.util.AttributeModifierUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class FrozenEffect extends MobEffect {
    private final String attributeModifierName = "frozen_movement_speed";
    public static final Map<UUID, FrozenPlayerData> frozenPlayers = new HashMap<>();
    public FrozenEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        ModNetworking.sendToAllPlayers(new FrozenDataS2C(pLivingEntity.getId(), true));

        if (!pLivingEntity.level().isClientSide()) {
            if(pLivingEntity.hasEffect(ModEffects.CHILLED.get())){
                pLivingEntity.removeEffect(ModEffects.CHILLED.get());
            }

            int duration = Objects.requireNonNull(pLivingEntity.getEffect(ModEffects.FROZEN.get())).getDuration();
            if (!pLivingEntity.level().isClientSide() && duration > 2) {
                //System.out.println("Freezing: " + pLivingEntity);
                //System.out.println("Duration: " + duration);
                if (pLivingEntity instanceof Player player) {
                    if (!frozenPlayers.containsKey(player.getUUID())) {
                        FrozenPlayerData data = new FrozenPlayerData(
                                player.getAbilities().getWalkingSpeed(), player.getAbilities().getFlyingSpeed(),
                                player.getX(), player.getY(), player.getZ());
                        frozenPlayers.put(player.getUUID(), data);
                        player.getAbilities().mayBuild = false;
                        player.getAbilities().setWalkingSpeed(0.0f);
                        if (!player.isCreative()) {
                            player.getAbilities().setFlyingSpeed(0);
                            player.getAbilities().invulnerable = false;
                        }
                        player.getAbilities().setWalkingSpeed(data.savedWalkingSpeed);
                        player.getAbilities().setFlyingSpeed(data.savedFlyingSpeed);
                        player.onUpdateAbilities();
                        player.setSprinting(false);
                        player.setJumping(false);
                    }

                    FrozenPlayerData data = frozenPlayers.get(player.getUUID());
                    if (data != null) {
                        player.teleportTo(data.savedX, data.savedY, data.savedZ);
                    }

                    player.hurtMarked = true;
                    AttributeModifierUtil.applyPermanentModifier(
                            pLivingEntity,
                            Attributes.MOVEMENT_SPEED,
                            attributeModifierName,
                            -1,
                            AttributeModifier.Operation.MULTIPLY_TOTAL);

                } else if (pLivingEntity instanceof Mob mob) {
                    Vec3 newVelocity = new Vec3(0, 0, 0);
                    mob.setDeltaMovement(newVelocity);
                    mob.setNoAi(true);
                    mob.hurtMarked = true;
                    mob.setSprinting(false);
                    mob.setJumping(false);
                    mob.getNavigation().stop();
                }
            }
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
        ModNetworking.sendToAllPlayers(new FrozenDataS2C(pLivingEntity.getId(), false));
        removeEntityFreeze(pLivingEntity);
        AttributeModifierUtil.removeModifier(
                pLivingEntity,
                Attributes.MOVEMENT_SPEED,
                attributeModifierName);
    }

    public static void removeEntityFreeze(LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof Player player) {
            frozenPlayers.remove(player.getUUID());
            player.getAbilities().mayBuild = true;
            if (player.isCreative()) {
                player.getAbilities().invulnerable = true;
            }
            player.hurtMarked = true;
        } else if (pLivingEntity instanceof Mob mob) {
            mob.setNoAi(false);
            mob.hurtMarked = true;
        }
    }

    public static class FrozenPlayerData {
        public final float savedWalkingSpeed;
        public final float savedFlyingSpeed;
        public final double savedX;
        public final double savedY;
        public final double savedZ;
        public FrozenPlayerData(float walkingSpeed, float flyingSpeed, double x, double y, double z) {
            this.savedWalkingSpeed = walkingSpeed;
            this.savedFlyingSpeed = flyingSpeed;
            this.savedX = x;
            this.savedY = y;
            this.savedZ = z;
        }
    }
}

