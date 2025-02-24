package net.cebularz.winterwonders.init;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
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
}
