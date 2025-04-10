package net.cebularz.winterwonders.events;

import net.cebularz.winterwonders.client.renderer.util.ParticleSpawnQueue;
import net.cebularz.winterwonders.init.ModDamageSources;
import net.cebularz.winterwonders.effect.ModEffects;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.SendParticlesS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.RevenantEntity;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID)
public class ModEvents {
    private static final String TAG_COLD_TIME = "ZombieColdTime";
    private static final int COLD_THRESHOLD = 600;
    private static final int SHAKE_DURATION = 100;
    private static boolean isPlaying = false;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Zombie zombie)) {
            return;
        }
       
        if (zombie instanceof RevenantEntity || zombie instanceof ZombieVillager) {
            return;
        }
        
        Level level = zombie.level();
        BlockPos pos = zombie.blockPosition();
        Biome biome = level.getBiome(pos).value();
        int coldTime = getColdTime(zombie, biome);

        boolean inTransformationPhase = (coldTime >= COLD_THRESHOLD - SHAKE_DURATION) && (coldTime < COLD_THRESHOLD);
        if (level.isClientSide && inTransformationPhase){
            float shakeAmount = 4.0F;
            zombie.setYRot(zombie.getYRot() + (zombie.level().random.nextFloat() - 0.5F) * shakeAmount);
            zombie.yRotO = zombie.getYRot();

            double shakePos = 0.05D;
            zombie.setPos(zombie.getX() + (zombie.level().random.nextDouble() - 0.5D) * shakePos,
                    zombie.getY(),
                    zombie.getZ() + (zombie.level().random.nextDouble() - 0.5D) * shakePos);
        } else if (!level.isClientSide && inTransformationPhase){
            zombie.removeFreeWill();
        }

        if (coldTime > COLD_THRESHOLD && !level.isClientSide) {
            RevenantEntity.convertZombieToRevenant(zombie);
        }
    }

    private static int getColdTime(Zombie zombie, Biome biome) {
        float temperature = biome.getBaseTemperature();
        boolean isCold = temperature < 0.15F;

        CompoundTag data = zombie.getPersistentData();
        int coldTime = data.getInt(TAG_COLD_TIME);
        if (isCold) {
            coldTime++;
        } else {
            coldTime = 0;
        }
        data.putInt(TAG_COLD_TIME, coldTime);
        return coldTime;
    }

    @SubscribeEvent
    public static void onEffectApplication (MobEffectEvent event){
        LivingEntity livingEntity = event.getEntity();
        MobEffectInstance mobEffectInstance = event.getEffectInstance();
        if (mobEffectInstance != null) {
            if (mobEffectInstance.getEffect() == ModEffects.CHILLED.get() && livingEntity.hasEffect(ModEffects.FROZEN.get())) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void  onEntityHurtPost(LivingDamageEvent event){
        LivingEntity hurtEntity = event.getEntity();
        Entity attackerEntity = event.getSource().getEntity();
        MobEffect frozenEffect = ModEffects.FROZEN.get();
        if (hurtEntity.hasEffect(frozenEffect) &&
                !event.getSource().typeHolder().equals(ModDamageSources.frostDamage(hurtEntity.level(), attackerEntity, null))){
            hurtEntity.removeEffect(frozenEffect);
            float entityMaxHealth = hurtEntity.getMaxHealth();
            int maxDamage = 10;
            //System.out.println("Dealing " + maxDamage + " damage to " + hurtEntity);
            hurtEntity.hurt(
                    ModDamageSources.frostDamage(
                            hurtEntity.level(),
                            attackerEntity,
                            null),
                    Math.min(maxDamage, entityMaxHealth / 4));
            hurtEntity.level().playSound(
                    null,
                    hurtEntity.getX(),
                    hurtEntity.getY(),
                    hurtEntity.getZ(),
                    SoundEvents.GLASS_BREAK,
                    SoundSource.AMBIENT,
                    1.25F,
                    0.4f / (hurtEntity.level().getRandom().nextFloat() * 0.4f + 0.8f)
            );
            double entitySize = hurtEntity.getBbHeight() * hurtEntity.getBbWidth();
            //System.out.println("Spawning " + (entitySize * 60) + " particles for " + hurtEntity.getName());
            RandomSource random = hurtEntity.level().getRandom();
            int count = (int) (entitySize * 60);
            for (int i = 0; i < count; i++) {
                double offX = (random.nextDouble() - 0.5) * 0.5;
                double offY = hurtEntity.getBbHeight() / 2;
                double offZ = (random.nextDouble() - 0.5) * 0.5;
                double theta = random.nextDouble() * Math.PI;
                double phi = random.nextDouble() * 2 * Math.PI;
                double speed = 0.5 + random.nextDouble() * 0.5;
                double xSpeed = speed * Math.sin(theta) * Math.cos(phi);
                double ySpeed = speed * Math.cos(theta);
                double zSpeed = speed * Math.sin(theta) * Math.sin(phi);
                ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.ICE.defaultBlockState());

                ModNetworking.sendToNear(new SendParticlesS2C(
                        particle,
                        hurtEntity.getX() + offX,
                        hurtEntity.getY() + offY,
                        hurtEntity.getZ() + offZ,
                        xSpeed, ySpeed, zSpeed), hurtEntity);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        ParticleSpawnQueue.tick();
    }
}
