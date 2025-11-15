package net.cebularz.winterwonders.item.custom;

import net.cebularz.winterwonders.client.CameraEngine;
import net.cebularz.winterwonders.client.shaders.blizzard.BlizzardRenderer;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.entity.custom.projectile.ChillingSnowballEntity;
import net.cebularz.winterwonders.entity.custom.projectile.IceCubeEntity;
import net.cebularz.winterwonders.entity.custom.projectile.IceSpikeProjectileEntity;
import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.item.custom.impl.IStaffItem;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.IceSpikeVisualS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.turtleboi.turtlecore.client.util.ParticleSpawnQueue;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.network.CoreNetworking;
import net.turtleboi.turtlecore.network.packet.util.SendParticlesS2C;
import net.turtleboi.turtlecore.particle.CoreParticles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LichBlizzardStaffItem extends Item implements IStaffItem {

    private int chargeTime = 0;
    private LichEntity caster;
    private final RandomSource random = RandomSource.create();
    private boolean interrupted = false;
    private StaffState currentState = StaffState.UNCHARGED;
    private static final int BLIZZARD_EFFECT_DURATION = 100;
    private static boolean iceProtectionOnCooldown = false;
    private static int iceProtectionCooldownTime = 0;
    private static final int ICE_PROTECTION_COOLDOWN = 600;
    private static final float ICE_PROTECTION_CHANCE = 0.3f;

    public LichBlizzardStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getChargeTime() {
        return chargeTime;
    }

    @Override
    public void setChargeTime(int chargeTime) {
        this.chargeTime = chargeTime;
    }

    public void setCaster(LichEntity caster) {
        this.caster = caster;
    }

    @Override
    public LivingEntity getCaster() {
        return this.caster;
    }

    @Override
    public boolean execute() {
        if (caster != null && caster.getTarget() != null) {
            executeBlizzardAttack(caster.getTarget());
            return true;
        }
        return false;
    }

    public void executeSnowballVolley(LivingEntity target) {
        if (caster == null) {
            return;
        } else {
            caster.level();
        }

        Level level = caster.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (int i = 0; i < 4; i++) {
            int delayMillis = i * (50 * 15);
            caster.getSpellScheduler().schedule(delayMillis, () -> {
                int chillAmplifier = 0;
                int damagePercent = 5;
                double accuracyModifier = 0.15;
                if (caster.getHealth() < (caster.getMaxHealth() / 2)) {
                    chillAmplifier = 2;
                    damagePercent = 10;
                    accuracyModifier = 0;
                }

                ChillingSnowballEntity snowball = new ChillingSnowballEntity(level, caster, chillAmplifier, true, damagePercent);

                double offsetX = random.nextGaussian() * accuracyModifier;
                double offsetY = target.getBbHeight() / 2;
                double offsetZ = random.nextGaussian() * accuracyModifier;

                Vec3 adjustedTargetPos = new Vec3(
                        target.getX() + offsetX,
                        target.getY() + offsetY,
                        target.getZ() + offsetZ
                );

                Vec3 directionVec = adjustedTargetPos.subtract(caster.position()).normalize();
                snowball.setDeltaMovement(directionVec.scale(1.5));

                level.addFreshEntity(snowball);

                serverLevel.sendParticles(
                        ParticleTypes.SNOWFLAKE,
                        caster.getX(), caster.getY() + 1.5, caster.getZ(),
                        10, 0.2, 0.2, 0.2, 0.1
                );
            });
        }
    }

    public void executeIceSpikeVolley(LivingEntity target) {
        if (caster == null) {
            return;
        } else {
            caster.level();
        }

        Level level = caster.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (int i = 0; i < 4; i++) {
            int delayMillis = i * (50 * 15);
            caster.getSpellScheduler().schedule(delayMillis, () -> {
                int damagePercent = 10;
                boolean homing = false;
                if (caster.getHealth() < (caster.getMaxHealth() / 2)) {
                    damagePercent = 20;
                    homing = true;
                }

                IceSpikeProjectileEntity iceSpike = new IceSpikeProjectileEntity(level, caster, (float) damagePercent / 100, homing);
                iceSpike.setHomingTarget(target);

                double offsetY = target.getBbHeight() / 2;

                Vec3 adjustedTargetPos = new Vec3(
                        target.getX(),
                        target.getY() + offsetY,
                        target.getZ()
                );

                Vec3 directionVec = adjustedTargetPos.subtract(caster.position()).normalize();
                iceSpike.setDeltaMovement(directionVec.scale(1.5));

                level.addFreshEntity(iceSpike);

                serverLevel.sendParticles(
                        CoreParticles.CHILLED_PARTICLES.get(),
                        caster.getX(), caster.getY() + 1.5, caster.getZ(),
                        20, 0.2, 0.2, 0.2, 0.1
                );
            });
        }
    }

    public void executeSpecialAttack(LivingEntity target) {
        if (caster == null) {
            return;
        } else {
            caster.level();
        }

        Level level = caster.level();

        if (level instanceof ServerLevel serverLevel) {
            int spikeCount = 5 + random.nextInt(3);

            for (int i = 0; i < spikeCount; i++) {
                final int index = i;
                serverLevel.getServer().tell(new TickTask(serverLevel.getServer().getTickCount() + (i * 3), () -> {
                    if (target.isAlive()) {
                        double offsetX = random.nextGaussian() * 1.5;
                        double offsetZ = random.nextGaussian() * 1.5;
                        BlockPos targetPos = new BlockPos(
                                (int)(target.getX() + offsetX),
                                (int)target.getY(),
                                (int)(target.getZ() + offsetZ)
                        );

                        for (int y = 0; y < 5; y++) {
                            serverLevel.sendParticles(
                                    ParticleTypes.ITEM_SNOWBALL,
                                    targetPos.getX(), targetPos.getY() + y, targetPos.getZ(),
                                    8, 0.2, 0.2, 0.2, 0.05
                            );
                        }

                        if (target.distanceToSqr(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < 4.0) {
                            float damage = 2.0f + (index * 0.5f);
                            target.hurt(target.damageSources().indirectMagic(caster, caster), damage);

                            if (random.nextFloat() < 0.55f) {
                                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                            }
                        }
                    }
                }));
            }

            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    caster.getX(), caster.getY() + 1.0, caster.getZ(),
                    20, 0.5, 0.5, 0.5, 0.1
            );
        }
    }

    public void executeTerrainAttack(LivingEntity target, boolean isCloseRange) {
        if (caster == null) {
            return;
        } else {
            caster.level();
        }

        Level level = caster.level();
        ServerLevel serverLevel = level instanceof ServerLevel ? (ServerLevel) level : null;
        if (serverLevel == null) return;

        if (isCloseRange) {
            executeIceSpikeAttack(serverLevel, target, 18, 8F);
        } else {
            BlockPos targetPos = target.blockPosition();
            int cubeCount = 3 + random.nextInt(3);

            for (int i = 0; i < cubeCount; i++) {
                int offsetX = random.nextInt(5) - 2;
                int offsetZ = random.nextInt(5) - 2;

                BlockPos cubePos = targetPos.offset(offsetX, 0, offsetZ);

                serverLevel.getServer().tell(new TickTask(
                        serverLevel.getServer().getTickCount() + (i * 5),
                        () -> {
                            for (int y = 0; y < 2; y++) {
                                serverLevel.sendParticles(
                                        ParticleTypes.SNOWFLAKE,
                                        cubePos.getX() + 0.5,
                                        cubePos.getY() + y,
                                        cubePos.getZ() + 0.5,
                                        10, 0.2, 0.2, 0.2, 0.02
                                );
                            }

                            IceCubeEntity entity = ModEntities.ICE_CUBE.get().create(level);
                            if (entity != null) {
                                entity.setPos(cubePos.getX() + 0.5, cubePos.getY() + 1, cubePos.getZ() + 0.5);
                                entity.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, 0.8F, 1.0F);
                                serverLevel.addFreshEntity(entity);
                            }
                        }
                ));
            }
        }
    }


    public void executeBlizzardAttack(LivingEntity target) {
        if (caster == null) {
            return;
        } else {
            caster.level();
        }

        Level level = caster.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos targetPos = target.blockPosition();

        int radius = 5;
        int duration = 100;

        startBlizzardEffectClient(caster.getOnPos().getCenter());
        serverLevel.sendParticles(
                ParticleTypes.EXPLOSION,
                targetPos.getX(), targetPos.getY() + 10, targetPos.getZ(),
                3, 0.5, 0.5, 0.5, 0.0
        );

        for (int tick = 0; tick < duration; tick += 5) {
            final int currentTick = tick;
            serverLevel.getServer().tell(new TickTask(
                    serverLevel.getServer().getTickCount() + tick,
                    () -> {
                        for (int i = 0; i < 30; i++) {
                            double x = targetPos.getX() + (random.nextDouble() * 2 - 1) * radius;
                            double z = targetPos.getZ() + (random.nextDouble() * 2 - 1) * radius;

                            serverLevel.sendParticles(
                                    ParticleTypes.SNOWFLAKE,
                                    x, targetPos.getY() + 10, z,
                                    5, 0.2, 0.5, 0.2, 0.1
                            );
                        }

                        if (currentTick % 20 == 0) {
                            for (int i = 0; i < 3; i++) {
                                double x = targetPos.getX() + (random.nextDouble() * 2 - 1) * radius * 0.8;
                                double z = targetPos.getZ() + (random.nextDouble() * 2 - 1) * radius * 0.8;

                                for (int y = 0; y < 10; y++) {
                                    serverLevel.sendParticles(
                                            ParticleTypes.ITEM_SNOWBALL,
                                            x, targetPos.getY() + 10 - y, z,
                                            2, 0.1, 0.1, 0.1, 0.05
                                    );
                                }

                                AABB impactArea = new AABB(
                                        x - 1, targetPos.getY(), z - 1,
                                        x + 1, targetPos.getY() + 2, z + 1
                                );

                                List<Entity> entities = serverLevel.getEntities(
                                        caster, impactArea, entity -> entity instanceof LivingEntity
                                );

                                for (Entity entity : entities) {
                                    if (entity instanceof Player victim) {
                                        victim.hurt(victim.damageSources().indirectMagic(caster, caster), 4.0f);
                                        victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                                        CameraEngine.getOrAssignEngine(victim).shakeScreen(3,100,0.23F);
                                    }
                                }
                            }
                        }

                        AABB effectArea = new AABB(
                                targetPos.getX() - radius, targetPos.getY(), targetPos.getZ() - radius,
                                targetPos.getX() + radius, targetPos.getY() + 5, targetPos.getZ() + radius
                        );

                        List<Entity> entities = serverLevel.getEntities(
                                caster, effectArea, entity -> entity instanceof LivingEntity
                        );

                        for (Entity entity : entities) {
                            if (entity instanceof LivingEntity victim && victim != caster) {
                                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                                if (currentTick % 20 == 0) {
                                    victim.hurt(victim.damageSources().indirectMagic(caster, caster), 1.0f);
                                }
                            }
                        }
                    }
            ));
        }
    }

    public void executeIceSpikeAttack(ServerLevel serverLevel, LivingEntity target, int spikeCount, float damage) {
        if (caster == null) {
            return;
        } else {
            caster.level();
        }

        Vec3 casterPos = caster.position();
        Vec3 targetPos = target.position();
        double dx = targetPos.x - casterPos.x;
        double dz = targetPos.z - casterPos.z;

        double centerAngle = Math.atan2(dz, dx);
        double distance = Math.sqrt(dx * dx + dz * dz);
        double baseRadius = Mth.clamp(distance, 1.0, 6.0);
        double arcRadians = Math.toRadians(90);
        boolean reverseSweep = random.nextBoolean();

        for (int i = 0; i < spikeCount; i++) {
            double t = (spikeCount == 1) ? 0.0 : ((double) i / (spikeCount - 1) - 0.5);
            double angle = centerAngle + t * arcRadians;

            double spikeX = casterPos.x + Math.cos(angle) * baseRadius;
            double spikeZ = casterPos.z + Math.sin(angle) * baseRadius;

            BlockPos spikePos = new BlockPos(
                    Mth.floor(spikeX),
                    caster.blockPosition().getY(),
                    Mth.floor(spikeZ)
            );

            double dxSpike = spikeX - casterPos.x;
            double dzSpike = spikeZ - casterPos.z;
            float yawDegrees = (float) Math.toDegrees(Math.atan2(dxSpike, dzSpike));
            int orderIndex = reverseSweep ? (spikeCount - 1 - i) : i;
            long baseDelay = orderIndex * 50L;

            for (int stage = 0; stage < 3; stage++) {
                int spellStage = stage;
                long delay = baseDelay + stage * (2 * 50L);
                caster.getSpellScheduler().schedule(delay,
                        () -> doIceSpikes(serverLevel, spikePos, damage, spellStage, yawDegrees));
            }
        }
    }

    private void doIceSpikes(ServerLevel serverLevel, BlockPos blockPos, float damage, int spellStage, float yawDegrees) {
        serverLevel.sendParticles(
                ParticleTypes.ITEM_SNOWBALL,
                blockPos.getX() + 0.5,
                blockPos.getY() + (spellStage * 0.5),
                blockPos.getZ() + 0.5,
                15, 0.2, 0.1, 0.2, 0.05
        );

        serverLevel.sendParticles(
                ParticleTypes.SNOWFLAKE,
                blockPos.getX() + 0.5,
                blockPos.getY() + ((spellStage * 0.5) + 0.25),
                blockPos.getZ() + 0.5,
                5, 0.3, 0.1, 0.3, 0.01
        );

        if (spellStage == 0) {
            serverLevel.playSound(
                    null,
                    blockPos,
                    SoundEvents.PLAYER_HURT_FREEZE,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.2F + serverLevel.getRandom().nextFloat() * 0.2F
            );

            BlockState blockState = serverLevel.getBlockState(blockPos.below());
            if (blockState.getFluidState().getType() == Fluids.WATER) {
                serverLevel.setBlockAndUpdate(blockPos.below(), Blocks.ICE.defaultBlockState());
                serverLevel.setBlockAndUpdate(blockPos, Blocks.ICE.defaultBlockState());
            } else if (blockState.hasProperty(BlockStateProperties.WATERLOGGED)
                    && blockState.getValue(BlockStateProperties.WATERLOGGED)) {
                serverLevel.setBlock(blockPos.below(),
                        blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.FALSE),
                        Block.UPDATE_ALL);
            }
        }

        if (spellStage == 2) {
            if (caster != null) {
                ModNetworking.sendNear(new IceSpikeVisualS2C(blockPos, yawDegrees), caster);
            }

            AABB damageArea = new AABB(
                    blockPos.getX() - 1, blockPos.getY(), blockPos.getZ() - 1,
                    blockPos.getX() + 1, blockPos.getY() + 2.5, blockPos.getZ() + 1
            );

            List<Entity> entities = serverLevel.getEntities(
                    caster, damageArea, entity -> entity instanceof LivingEntity
            );

            for (Entity entity : entities) {
                if (entity instanceof LivingEntity victim) {
                    if (caster != null && caster.isMinion(caster, victim)) {
                        continue;
                    }

                    victim.hurt(victim.damageSources().indirectMagic(caster, caster), damage);
                    Vec3 knockbackDir;
                    if (caster != null) {
                        knockbackDir = victim.position().subtract(caster.position());
                    } else {
                        knockbackDir = new Vec3(
                                victim.getX() - (blockPos.getX() + 0.5),
                                0.0,
                                victim.getZ() - (blockPos.getZ() + 0.5)
                        );
                    }

                    if (knockbackDir.lengthSqr() < 1.0e-4) {
                        knockbackDir = new Vec3(0.0, 0.0, 1.0);
                    }

                    knockbackDir = knockbackDir.normalize();
                    Vec3 knockback = new Vec3(knockbackDir.x * 1.1, 0.35, knockbackDir.z * 1.1);

                    victim.setDeltaMovement(victim.getDeltaMovement().add(knockback));
                    victim.hurtMarked = true;
                    victim.addEffect(new MobEffectInstance(
                            CoreEffects.CHILLED.get(),
                            120,
                            1
                    ));

                    serverLevel.playSound(
                            null,
                            victim.blockPosition(),
                            SoundEvents.PLAYER_HURT_FREEZE,
                            SoundSource.PLAYERS,
                            0.8F,
                            1.0F
                    );
                }
            }
        }
    }

    public void executeWhirlwindAttack() {
        if (caster == null) {
            return;
        }

        Level level = caster.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (int i = 0; i < 4; i++) {
            serverLevel.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    caster.getX(), (caster.getY() + caster.getBbHeight() / 2), caster.getZ(),
                    20, 1.0, 0.5, 1.0, 0.2
            );
        }

        double maxRadius = 6.0;
        long startTime = System.currentTimeMillis();
        int totalDegrees = 360;
        double rotationSpeed = Math.toRadians(totalDegrees);
        int stepAdvanceInterval = 3;
        int minParticles = 1;
        int maxParticles = 24;

        for (int step = 0; step < totalDegrees; step += stepAdvanceInterval) {
            int delay = step * stepAdvanceInterval;
            int finalStep = step;
            ParticleSpawnQueue.schedule(delay, () -> {
                double timeElapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                double particleAngle = Math.toRadians(finalStep) + rotationSpeed * timeElapsed;
                double progress = Math.min(1.0, timeElapsed / ((double) (totalDegrees * stepAdvanceInterval) / 1000.0));
                double newRadius = maxRadius * progress;
                double x = caster.getX() + Math.sin(particleAngle) * newRadius;
                double y = caster.getY() + (timeElapsed * 5);
                double z = caster.getZ() + Math.cos(particleAngle) * newRadius;

                int particleCount = (int) (minParticles + (maxParticles - minParticles) * progress);
                double spread = 0.25 + (1.0 - 0.25) * progress;
                for (int i = 0; i < particleCount; i++) {
                    double particleX = x + (random.nextDouble() - 0.5) * spread;
                    double particleY = caster.getY() + (random.nextDouble() - 0.5) * spread + (timeElapsed * 5);
                    double particleZ = z + (random.nextDouble() - 0.5) * spread;

                    CoreNetworking.sendToNear(new SendParticlesS2C(
                            CoreParticles.CHILLED_PARTICLES.get(),
                            particleX, particleY, particleZ,
                            0, 0, 0), caster);
                }

                AABB whirlwindArea = caster.getBoundingBox().inflate(maxRadius);

                List<LivingEntity> targets = caster.level().getEntitiesOfClass(LivingEntity.class, whirlwindArea);
                double attractionStrength = Math.max(0.02, 0.05 * progress);
                for (LivingEntity livingTargets : targets) {
                    if (livingTargets != caster && !caster.isMinion(caster, livingTargets)) {
                        double dx = x - livingTargets.getX();
                        double dy = y - livingTargets.getY();
                        double dz = z - livingTargets.getZ();

                        livingTargets.setDeltaMovement(livingTargets.getDeltaMovement().add(
                                dx * attractionStrength, dy * attractionStrength, dz * attractionStrength));
                        livingTargets.hurtMarked = true;

                        if (!livingTargets.hasEffect(CoreEffects.CHILLED.get())){
                            livingTargets.addEffect(new MobEffectInstance(
                                    CoreEffects.CHILLED.get(),
                                    300,
                                    2
                            ));
                        } else if (livingTargets.hasEffect(CoreEffects.CHILLED.get()) && livingTargets.getEffect(CoreEffects.CHILLED.get()).getAmplifier() <= 2) {
                            livingTargets.addEffect(new MobEffectInstance(
                                    CoreEffects.CHILLED.get(),
                                    300,
                                    2,
                                    livingTargets.getEffect(CoreEffects.CHILLED.get()).isAmbient(),
                                    livingTargets.getEffect(CoreEffects.CHILLED.get()).isVisible(),
                                    livingTargets.getEffect(CoreEffects.CHILLED.get()).showIcon()
                            ));
                        }
                    }
                }
            });
        }
    }

    public void createRotatingIceBlocks(LivingEntity lich, int blockCount, float duration) {
        if (lich == null) {
            return;
        } else {
            lich.level();
        }

        Level level = lich.level();
        ServerLevel serverLevel = level instanceof ServerLevel ? (ServerLevel) level : null;
        if (serverLevel == null) return;

        List<IceBlockTracker> iceBlocks = new ArrayList<>();

        for (int i = 0; i < blockCount; i++) {
            final int blockIndex = i;
            int spawnDelay = i * 4;

            serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                    serverLevel.getServer().getTickCount() + spawnDelay,
                    () -> {
                        double angle = (Math.PI * 2 * blockIndex) / blockCount;
                        double radius = 2.5;
                        double x = lich.getX() + Math.cos(angle) * radius;
                        double z = lich.getZ() + Math.sin(angle) * radius;
                        BlockPos blockPos = new BlockPos((int)x, (int)lich.getY(), (int)z);

                        IceBlockTracker tracker = new IceBlockTracker(
                                blockPos,
                                angle,
                                radius,
                                serverLevel.getGameTime()
                        );
                        iceBlocks.add(tracker);

                        playIceFormationEffect(serverLevel, blockPos);
                    }
            ));
        }

        int totalTicks = (int)(duration * 20);

        ScheduledTask rotationTask = new ScheduledTask(serverLevel, totalTicks, (currentTick) -> {
            for (IceBlockTracker block : iceBlocks) {

                float elapsedTime = (serverLevel.getGameTime() - block.spawnTime) / 20f;
                double rotationSpeed = 0.5;
                double newAngle = block.initialAngle + (rotationSpeed * elapsedTime);

                double x = lich.getX() + Math.cos(newAngle) * block.radius;
                double z = lich.getZ() + Math.sin(newAngle) * block.radius;

                double maxHeight = 1.5;
                double height = Math.min(maxHeight, elapsedTime * 0.5);
                double y = lich.getY() + height;

                BlockPos newPos = new BlockPos((int)x, (int)y, (int)z);
                displayIceBlock(serverLevel, newPos);

                AABB blockBox = new AABB(
                        x - 0.5, y - 0.5, z - 0.5,
                        x + 0.5, y + 0.5, z + 0.5
                );

                List<Entity> collidingEntities = serverLevel.getEntities(
                        lich, blockBox, entity -> entity instanceof LivingEntity && entity != lich
                );

                for (Entity entity : collidingEntities) {
                    if (entity instanceof LivingEntity victim) {
                        victim.hurt(victim.damageSources().freeze(), 2.0f);

                        victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));

                        serverLevel.playSound(
                                null,
                                victim.blockPosition(),
                                SoundEvents.PLAYER_HURT_FREEZE,
                                SoundSource.NEUTRAL,
                                0.6F,
                                1.0F + serverLevel.getRandom().nextFloat() * 0.2F
                        );

                        Vec3 knockback = new Vec3(
                                victim.getX() - x,
                                0.1,
                                victim.getZ() - z
                        ).normalize().scale(0.3);
                        victim.setDeltaMovement(victim.getDeltaMovement().add(knockback));
                    }
                }
            }

            return currentTick < totalTicks;
        });

        rotationTask.start();
    }

    public void tick(Level level, LichEntity entity, int hurtAmount) {
        if (iceProtectionOnCooldown) {
            iceProtectionCooldownTime--;
            if (iceProtectionCooldownTime <= 0) {
                iceProtectionOnCooldown = false;
            }
        }

        if (!iceProtectionOnCooldown &&
                entity.getHealth() < entity.getMaxHealth() * 0.5f &&
                hurtAmount > 3.0f &&
                level.getRandom().nextFloat() < ICE_PROTECTION_CHANCE) {

            createRotatingIceBlocks(entity,5, Float.MAX_VALUE);

            iceProtectionOnCooldown = true;
            iceProtectionCooldownTime = ICE_PROTECTION_COOLDOWN;

            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    SoundEvents.WITHER_HURT,
                    SoundSource.HOSTILE,
                    1.0F,
                    0.5F
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void startBlizzardEffectClient(Vec3 center) {
        BlizzardRenderer.getInstance().startBlizzard(center, BLIZZARD_EFFECT_DURATION, 1.0f);
    }

    @Override
    public boolean isInterrupted() {
        return this.interrupted;
    }

    @Override
    public void onInterrupted() {
        this.interrupted = true;
    }

    @Override
    public StaffState getCurrentState() {
        return currentState;
    }

    @Override
    public void setCurrentStaffState(StaffState state) {
        this.currentState = state;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    // Helper class to track each ice block
    private static class IceBlockTracker {
        public BlockPos initialPos;
        public double initialAngle;
        public double radius;
        public long spawnTime;

        public IceBlockTracker(BlockPos pos, double angle, double radius, long time) {
            this.initialPos = pos;
            this.initialAngle = angle;
            this.radius = radius;
            this.spawnTime = time;
        }
    }

    // Helper class for scheduled tasks that run over multiple ticks
    private static class ScheduledTask {
        private final ServerLevel level;
        private final int maxTicks;
        private final Function<Integer, Boolean> updateFunction;
        private int currentTick = 0;

        public ScheduledTask(ServerLevel level, int maxTicks, Function<Integer, Boolean> updateFunction) {
            this.level = level;
            this.maxTicks = maxTicks;
            this.updateFunction = updateFunction;
        }

        public void start() {
            scheduleNextTick();
        }

        private void scheduleNextTick() {
            level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + 1,
                    () -> {
                        boolean continueTask = updateFunction.apply(currentTick);
                        currentTick++;

                        if (continueTask && currentTick < maxTicks) {
                            scheduleNextTick();
                        } else {

                        }
                    }
            ));
        }
    }

    private void playIceFormationEffect(ServerLevel level, BlockPos pos) {
        level.playSound(
                null,
                pos,
                SoundEvents.GLASS_PLACE,
                SoundSource.BLOCKS,
                1.0F,
                0.8F + level.getRandom().nextFloat() * 0.4F
        );

        level.sendParticles(
                ParticleTypes.SNOWFLAKE,
                pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                20, 0.7, 0.1, 0.7, 0.02
        );

        for (int y = 0; y < 3; y++) {
            final int yOffset = y;
            level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + y * 3,
                    () -> {

                    }
            ));
        }
    }

    private void displayIceBlock(ServerLevel level, BlockPos pos) {
        double size = 0.5;

        for (int i = 0; i < 8; i++) {
            double xOffset = ((i & 1) == 0) ? -size : size;
            double yOffset = ((i & 2) == 0) ? -size : size;
            double zOffset = ((i & 4) == 0) ? -size : size;

            IceCubeEntity entity = ModEntities.ICE_CUBE.get().create(level);
            entity.setPos(xOffset,yOffset,zOffset);
            level.addFreshEntity(entity);
        }

        level.sendParticles(
                ParticleTypes.SNOWFLAKE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                3, 0.4, 0.4, 0.4, 0.01
        );
    }

}