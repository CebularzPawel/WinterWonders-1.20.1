package net.cebularz.winterwonders.item.custom;

import net.cebularz.winterwonders.client.CameraEngine;
import net.cebularz.winterwonders.client.renderer.util.ParticleSpawnQueue;
import net.cebularz.winterwonders.client.shaders.blizzard.BlizzardRenderer;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.entity.custom.projectile.ChillingSnowballEntity;
import net.cebularz.winterwonders.entity.custom.projectile.IceCubeEntity;
import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.init.ModEntities;
import net.cebularz.winterwonders.item.custom.impl.IStaffItem;
import net.cebularz.winterwonders.network.ModNetworking;
import net.cebularz.winterwonders.network.packets.SendParticlesS2C;
import net.cebularz.winterwonders.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    public void executeProjectileAttack(LivingEntity target, int variant) {
        if (caster == null || caster.level() == null) return;

        Level level = caster.level();

        for (int i = 0; i < 3; i++) {
            ChillingSnowballEntity snowball = new ChillingSnowballEntity(level, caster);

            double offsetX = random.nextGaussian() * 0.2;
            double offsetY = random.nextGaussian() * 0.1;
            double offsetZ = random.nextGaussian() * 0.2;

            Vec3 directionVec = target.position().subtract(caster.position()).normalize();

            snowball.setDeltaMovement(
                    directionVec.x + offsetX,
                    directionVec.y + 0.1 + offsetY,
                    directionVec.z + offsetZ
            );

            snowball.setDeltaMovement(snowball.getDeltaMovement().scale(1.5));

            snowball.addTag("freezeSnowball");

            level.addFreshEntity(snowball);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.SNOWFLAKE,
                        caster.getX(), caster.getY() + 1.5, caster.getZ(),
                        10, 0.2, 0.2, 0.2, 0.1
                );
            }
        }
    }

    public void executeSpecialAttack(LivingEntity target) {
        if (caster == null || caster.level() == null) return;

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
            createIcySpikeFloor(target, 16, 5.9F);
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
                                    if (entity instanceof LivingEntity victim) {
                                        victim.hurt(victim.damageSources().indirectMagic(caster, caster), 4.0f);
                                        victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
                                        CameraEngine.getOrAssignEngine((Player) victim).shakeScreen(3,100,0.23F);
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

    public void createIcySpikeFloor(LivingEntity target, int radius, float damage) {
        if (caster == null) {
            return;
        } else {
            caster.level();
        }

        Level level = caster.level();
        ServerLevel serverLevel = level instanceof ServerLevel ? (ServerLevel) level : null;
        if (serverLevel == null) return;

        BlockPos targetPos = target.blockPosition();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x*x + z*z);
                if (distance <= radius && random.nextFloat() < 0.7f) {
                    BlockPos spikePos = targetPos.offset(x, 0, z);

                    int delay = (int)(distance * 3);

                    serverLevel.getServer().tell(new TickTask(
                            serverLevel.getServer().getTickCount() + delay,
                            () -> createIceSpike(serverLevel, spikePos, damage)
                    ));
                }
            }
        }
    }

    private void createIceSpike(ServerLevel serverLevel, BlockPos pos, float damage) {
        for (int i = 0; i < 3; i++) {
            final int yOffset = i;
            serverLevel.getServer().tell(new TickTask(
                    serverLevel.getServer().getTickCount() + i * 2,
                    () -> {
                        serverLevel.sendParticles(
                                ParticleTypes.ITEM_SNOWBALL,
                                pos.getX() + 0.5, pos.getY() + yOffset * 0.5, pos.getZ() + 0.5,
                                15, 0.2, 0.1, 0.2, 0.05
                        );

                        serverLevel.sendParticles(
                                ParticleTypes.SNOWFLAKE,
                                pos.getX() + 0.5, pos.getY() + yOffset * 0.5 + 0.25, pos.getZ() + 0.5,
                                5, 0.3, 0.1, 0.3, 0.01
                        );

                        if (yOffset == 0) {
                            serverLevel.playSound(
                                    null,
                                    pos,
                                    SoundEvents.GLASS_PLACE,
                                    SoundSource.BLOCKS,
                                    1.0F,
                                    1.2F + serverLevel.getRandom().nextFloat() * 0.2F
                            );
                        }

                        if (yOffset == 2) {
                            AABB damageArea = new AABB(
                                    pos.getX(), pos.getY(), pos.getZ(),
                                    pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1
                            );

                            List<Entity> entities = serverLevel.getEntities(
                                    caster, damageArea, entity -> entity instanceof LivingEntity
                            );

                            for (Entity entity : entities) {
                                if (entity instanceof LivingEntity victim) {
                                    victim.hurt(victim.damageSources().indirectMagic(caster, caster), damage);

                                    Vec3 knockback = new Vec3(
                                            victim.getX() - pos.getX() - 0.5,
                                            0.2,
                                            victim.getZ() - pos.getZ() - 0.5
                                    ).normalize().scale(0.4);
                                    victim.setDeltaMovement(victim.getDeltaMovement().add(knockback));

                                    victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));

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
            ));
        }
    }

    public void executeWhirlwindAttack(LivingEntity target) {
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

                    ModNetworking.sendToNear(new SendParticlesS2C(
                            ModParticles.CHILLED_PARTICLES.get(),
                            particleX, particleY, particleZ,
                            0, 0, 0), caster);
                }

                AABB whirlwindArea = caster.getBoundingBox().inflate(maxRadius);

                List<LivingEntity> targets = caster.level().getEntitiesOfClass(LivingEntity.class, whirlwindArea);
                double attractionStrength = Math.max(0.02, 0.05 * progress);
                for (LivingEntity livingTargets : targets) {
                    if (livingTargets != caster) {
                        double dx = x - livingTargets.getX();
                        double dy = y - livingTargets.getY();
                        double dz = z - livingTargets.getZ();

                        livingTargets.setDeltaMovement(livingTargets.getDeltaMovement().add(
                                dx * attractionStrength, dy * attractionStrength, dz * attractionStrength));
                        livingTargets.hurtMarked = true;

                        if (!livingTargets.hasEffect(ModEffects.CHILLED.get())){
                            livingTargets.addEffect(new MobEffectInstance(
                                    ModEffects.CHILLED.get(),
                                    300,
                                    2
                            ));
                        } else if (livingTargets.hasEffect(ModEffects.CHILLED.get()) && livingTargets.getEffect(ModEffects.CHILLED.get()).getAmplifier() <= 2) {
                            livingTargets.addEffect(new MobEffectInstance(
                                    ModEffects.CHILLED.get(),
                                    300,
                                    2,
                                    livingTargets.getEffect(ModEffects.CHILLED.get()).isAmbient(),
                                    livingTargets.getEffect(ModEffects.CHILLED.get()).isVisible(),
                                    livingTargets.getEffect(ModEffects.CHILLED.get()).showIcon()
                            ));
                        }
                    }
                }
            });
        }
    }

    public void createRotatingIceBlocks(LivingEntity lich, int blockCount, float duration) {
        if (lich == null || lich.level() == null) return;

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