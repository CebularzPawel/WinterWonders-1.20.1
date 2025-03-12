package net.cebularz.winterwonders.item.custom;

import net.cebularz.winterwonders.client.shaders.blizzard.BlizzardRenderer;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.entity.custom.projectile.ChillingSnowballEntity;
import net.cebularz.winterwonders.entity.custom.projectile.IceSpikeProjectileEntity;
import net.cebularz.winterwonders.init.ModEffects;
import net.cebularz.winterwonders.item.custom.impl.IStaffItem;
import net.cebularz.winterwonders.networking.ClientboundBlizzardPacket;
import net.cebularz.winterwonders.networking.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class LichBlizzardStaffItem extends Item implements IStaffItem {

    private int chargeTime = 0;
    private LichEntity caster;
    private final RandomSource random = RandomSource.create();
    private boolean interrupted = false;
    private StaffState currentState = StaffState.UNCHARGED;
    private static final int BLIZZARD_EFFECT_DURATION = 100;

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
                serverLevel.getServer().tell(new net.minecraft.server.TickTask(serverLevel.getServer().getTickCount() + (i * 3), () -> {
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
        if (caster == null || caster.level() == null) return;

        Level level = caster.level();
        ServerLevel serverLevel = level instanceof ServerLevel ? (ServerLevel) level : null;
        if (serverLevel == null) return;

        if (isCloseRange) {

            BlockPos targetPos = target.blockPosition();

            int radius = 3;
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (random.nextFloat() < 0.7f) {
                        BlockPos spikePos = targetPos.offset(x, 0, z);

                        int delay = (int)(Math.sqrt(x*x + z*z) * 2);

                        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                                serverLevel.getServer().getTickCount() + delay,
                                () -> {
                                    // Particles for spike
                                    for (int y = 0; y < 3; y++) {
                                        serverLevel.sendParticles(
                                                ParticleTypes.ITEM_SNOWBALL,
                                                spikePos.getX() + 0.5, spikePos.getY() + y, spikePos.getZ() + 0.5,
                                                10, 0.2, 0.4, 0.2, 0.1
                                        );
                                    }

                                    AABB searchBox = new AABB(
                                            spikePos.getX(), spikePos.getY(), spikePos.getZ(),
                                            spikePos.getX() + 1, spikePos.getY() + 2, spikePos.getZ() + 1
                                    );

                                    List<Entity> entities = serverLevel.getEntities(
                                            caster, searchBox, entity -> entity instanceof LivingEntity
                                    );

                                    for (Entity entity : entities) {
                                        if (entity instanceof LivingEntity victim) {
                                            victim.hurt(victim.damageSources().indirectMagic(caster, caster), 3.0f);
                                        }
                                    }
                                }
                        ));
                    }
                }
            }
        } else {
            BlockPos targetPos = target.blockPosition();

            int cubeCount = 3 + random.nextInt(3);
            for (int i = 0; i < cubeCount; i++) {
                int offsetX = random.nextInt(5) - 2;
                int offsetZ = random.nextInt(5) - 2;

                BlockPos cubePos = targetPos.offset(offsetX, 0, offsetZ);

                serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                        serverLevel.getServer().getTickCount() + (i * 5),
                        () -> {
                            for (int y = 0; y < 2; y++) {
                                for (int j = 0; j < 20; j++) {
                                    serverLevel.sendParticles(
                                            ParticleTypes.SNOWFLAKE,
                                            cubePos.getX() + random.nextFloat(),
                                            cubePos.getY() + y,
                                            cubePos.getZ() + random.nextFloat(),
                                            1, 0.1, 0.1, 0.1, 0.01
                                    );
                                }
                            }

                            AABB searchBox = new AABB(
                                    cubePos.getX() - 0.5, cubePos.getY(), cubePos.getZ() - 0.5,
                                    cubePos.getX() + 1.5, cubePos.getY() + 2, cubePos.getZ() + 1.5
                            );

                            List<Entity> entities = serverLevel.getEntities(
                                    caster, searchBox, entity -> entity instanceof LivingEntity
                            );

                            for (Entity entity : entities) {
                                if (entity instanceof LivingEntity victim) {
                                    victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                                }
                            }

                        }
                ));
            }
        }
    }

    public void executeBlizzardAttack(LivingEntity target) {
        if (caster == null || caster.level() == null) return;

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
            serverLevel.getServer().tell(new net.minecraft.server.TickTask(
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

    public void executeWhirlwindAttack(LivingEntity target) {
        if (caster == null || caster.level() == null) return;

        Level level = caster.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        int duration = 60;
        double pushStrength = 0.3;

        serverLevel.sendParticles(
                ParticleTypes.CLOUD,
                caster.getX(), caster.getY() + 1, caster.getZ(),
                20, 1.0, 0.5, 1.0, 0.2
        );

        for (int tick = 0; tick < duration; tick += 2) {
            final int currentTick = tick;
            serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                    serverLevel.getServer().getTickCount() + tick,
                    () -> {
                        float angle = (float) (currentTick * 0.2);
                        float xForce = Mth.sin(angle) * (float)pushStrength;
                        float zForce = Mth.cos(angle) * (float)pushStrength;

                        double radius = 5.0;

                        for (int i = 0; i < 10; i++) {
                            double particleAngle = Math.PI * 2 * (i / 10.0) + (currentTick * 0.1);
                            double x = caster.getX() + Math.sin(particleAngle) * radius;
                            double z = caster.getZ() + Math.cos(particleAngle) * radius;

                            serverLevel.sendParticles(
                                    ParticleTypes.CLOUD,
                                    x, caster.getY() + 1, z,
                                    1, 0.1, 0.1, 0.1, 0.2
                            );
                        }

                        AABB effectArea = new AABB(
                                caster.getX() - radius, caster.getY(), caster.getZ() - radius,
                                caster.getX() + radius, caster.getY() + 3, caster.getZ() + radius
                        );

                        List<Entity> entities = serverLevel.getEntities(
                                caster, effectArea, entity -> entity instanceof LivingEntity && entity != caster
                        );

                        for (Entity entity : entities) {
                            if (entity instanceof LivingEntity victim) {
                                double dx = entity.getX() - caster.getX();
                                double dz = entity.getZ() - caster.getZ();
                                double distSq = dx * dx + dz * dz;

                                if (distSq > 0) {
                                    double dist = Math.sqrt(distSq);
                                    double nx = dx / dist;
                                    double nz = dz / dist;

                                    double pushX = (nx * 0.5 + xForce) * pushStrength;
                                    double pushZ = (nz * 0.5 + zForce) * pushStrength;

                                    victim.setDeltaMovement(
                                            victim.getDeltaMovement().x + pushX,
                                            victim.getDeltaMovement().y + 0.1,
                                            victim.getDeltaMovement().z + pushZ
                                    );

                                    if (victim instanceof Player) {
                                        ((Player) victim).fallDistance = 0;
                                    }
                                }
                            }
                        }

                        if (currentTick % 10 == 0 && target.isAlive()) {
                            for (int i = 0; i < 2; i++) {
                                ChillingSnowballEntity snowball = new ChillingSnowballEntity(level, caster);

                                double spawnAngle = random.nextDouble() * Math.PI * 2;
                                double spawnX = caster.getX() + Math.sin(spawnAngle) * radius;
                                double spawnY = caster.getY() + 1.5;
                                double spawnZ = caster.getZ() + Math.cos(spawnAngle) * radius;

                                snowball.setPos(spawnX, spawnY, spawnZ);

                                Vec3 directionVec = target.position().subtract(snowball.position()).normalize();

                                directionVec = directionVec.add(
                                        random.nextGaussian() * 0.1,
                                        random.nextGaussian() * 0.05 + 0.1,
                                        random.nextGaussian() * 0.1
                                ).normalize();

                                snowball.setDeltaMovement(directionVec.scale(1.2));

                                snowball.addTag("freezeSnowball");

                                level.addFreshEntity(snowball);
                            }
                        }
                    }
            ));
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
}