package net.turtleboi.winterwonders.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.*;

public class CursedPortalEntity extends Entity {
    private static final EntityDataAccessor<Integer> TEXTURE_INDEX = SynchedEntityData.defineId(CursedPortalEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TELEPORT_ENABLED = SynchedEntityData.defineId(CursedPortalEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SPAWNING_ENABLED = SynchedEntityData.defineId(CursedPortalEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockPos> ALTAR_POS = SynchedEntityData.defineId(CursedPortalEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Optional<UUID>> LINKED_PORTAL_UUID = SynchedEntityData.defineId(CursedPortalEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final int teleportCooldown = 100;
    private static  int spawningCooldown = 0;
    private Map<UUID, Integer> playerCooldowns = new HashMap<>();
    private static int portalLiveTime = 620;

    private BlockPos altarPos;
    private CursedPortalEntity linkedPortal;
    private Object owner;
    private int textureTickCounter = 0;
    protected int age;


    public CursedPortalEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(TEXTURE_INDEX, 0);
        this.entityData.define(TELEPORT_ENABLED, false);
        this.entityData.define(SPAWNING_ENABLED, false);
        this.entityData.define(ALTAR_POS, BlockPos.ZERO);
        this.entityData.define(LINKED_PORTAL_UUID, Optional.empty());
    }

    @Override
    public void tick() {
        super.tick();
        age = this.tickCount;

        if (this.level().isClientSide) {
            if (age % 20 == 0) {
                spawnPortalParticles();
            }
        }

        if (!this.level().isClientSide) {
            detectAndApplyScreenEffect();
            decrementCooldowns();
            textureTickCounter++;
            int ticksPerFrame = 2;
            if (textureTickCounter >= ticksPerFrame) {
                int newIndex = (this.getTextureIndex() + 1) % getMaxTextureIndex();
                this.setTextureIndex(newIndex);
                textureTickCounter = 0;
            }

            if (age == 1){
                playPortalSpawnSound();
            }

            if (age % 20 == 0) {
                spawnPortalParticles();
            }

            if (age % 100 == 0) {
                playPortalAmbientSound();
            }

            if (this.isSpawningEnabled() && spawningCooldown <= 0){
                this.level().playSound(
                        null,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        SoundEvents.CHORUS_FRUIT_TELEPORT,
                        SoundSource.HOSTILE,
                        1.00F,
                        this.level().random.nextFloat() * 0.2F + 0.5F
                );
                //EliminationTrial.MobList mobList = new EliminationTrial.MobList();
                //EliminationTrial.WeightedMob WMob = mobList.selectRandomTargetMobWithWeight();
                //EntityType<?> mob = WMob.getMobType();
                //spawningCooldown = getCooldown(WMob.getWeight());
                //spawnMob(mob);
            }
            spawningCooldown--;

            if (this.isTeleportEnabled()) {
                teleportNearbyPlayers();
            }

            if (age >= portalLiveTime){
                this.discard();
            }

            if
             (getOwner() == null){
                this.discard();
            }
        }
    }

    public void setOwner(Object pOwner) {
        if (pOwner instanceof Entity || pOwner instanceof BlockEntity) {
            this.owner = pOwner;
        }
    }

    public Object getOwner() {
        return this.owner;
    }

    private void spawnPortalParticles() {
        RandomSource random = this.level().getRandom();
        double portalX = this.getX();
        double portalZ = this.getZ();
        double portalHeightStart = this.getY();
        int numParticles = 8;

        for (int i = 0; i < numParticles; i++) {
            double randomY = portalHeightStart + random.nextDouble() * 2.5;
            double randomX = portalX + (random.nextDouble() - 0.5) * 1.25;
            double randomZ = portalZ + (random.nextDouble() - 0.5) * 1.25;
            double velocityX = (random.nextDouble() - 0.5) * 0.05;
            double velocityY = (random.nextDouble() - 0.5) * 0.05;
            double velocityZ = (random.nextDouble() - 0.5) * 0.05;
            this.level().addParticle(
                    new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F),
                    randomX,
                    randomY,
                    randomZ,
                    velocityX,
                    velocityY,
                    velocityZ);
        }
    }

    private void playPortalSpawnSound() {
        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.BLOCKS,
                1.0F,
                this.level().random.nextFloat() * 0.2F + 0.5F
        );
    }

    private void playPortalAmbientSound() {
        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.PORTAL_AMBIENT,
                SoundSource.BLOCKS,
                0.5F,
                this.level().random.nextFloat() * 0.4F + 0.8F);
    }

    private void playPortalTravelSound(Player player) {
        this.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                SoundSource.BLOCKS,
                0.75F,
                this.level().random.nextFloat() * 0.2F + 0.5F
        );
    }

    private void decrementCooldowns() {
        playerCooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);
        playerCooldowns.replaceAll((uuid, cooldown) -> cooldown - 1);
    }

    private void teleportNearbyPlayers() {
        AABB detectionBox = this.getBoundingBox();
        List<Player> players = this.level().getEntitiesOfClass(Player.class, detectionBox);

        for (Player player : players) {
            if (player instanceof ServerPlayer serverPlayer) {
                UUID playerUUID = serverPlayer.getUUID();
                if (playerCooldowns.getOrDefault(playerUUID, 0) > 0) {
                    continue;
                }
                if (this.linkedPortal != null && this.linkedPortal.isTeleportEnabled()) {
                    this.getLinkedPortal().playerCooldowns.put(playerUUID, teleportCooldown);
                    teleportPlayerToPortal(serverPlayer, this.getLinkedPortal().blockPosition(), this.level());
                } else if (this.altarPos != null) {
                    teleportPlayerToAltar(serverPlayer, altarPos, this.level());
                }
                playerCooldowns.put(playerUUID, teleportCooldown);
            }
        }
    }

    private void spawnMob(EntityType<?> WantToBeAMob){
        Entity spawnedMob = WantToBeAMob.create(this.level());

        if (spawnedMob != null) {
            if(spawnedMob instanceof Slime slimeMob){
                slimeMob.setSize(4,true);
            }

            //if(spawnedMob instanceof Zombie || spawnedMob instanceof Skeleton || spawnedMob instanceof Husk ||
            //        spawnedMob instanceof Stray || spawnedMob instanceof WitherSkeleton || spawnedMob instanceof Piglin ||
            //        spawnedMob instanceof ZombifiedPiglin || spawnedMob instanceof PiglinBrute){
            //    spawnedMob.setItemSlot(EquipmentSlot.CHEST, Items.GOLDEN_CHESTPLATE.getDefaultInstance());
            //}

            if(spawnedMob instanceof Skeleton || spawnedMob instanceof Stray){
                spawnedMob.setItemSlot(EquipmentSlot.MAINHAND, Items.BOW.getDefaultInstance());
            }

            if(spawnedMob instanceof WitherSkeleton){
                spawnedMob.setItemSlot(EquipmentSlot.MAINHAND, Items.STONE_SWORD.getDefaultInstance());
            }

            if(spawnedMob instanceof ZombifiedPiglin){
                spawnedMob.setItemSlot(EquipmentSlot.MAINHAND, Items.GOLDEN_SWORD.getDefaultInstance());
            }

            if(spawnedMob instanceof Piglin || spawnedMob instanceof PiglinBrute){
                spawnedMob.setItemSlot(EquipmentSlot.MAINHAND, Items.GOLDEN_AXE.getDefaultInstance());
            }

            if(spawnedMob instanceof Vindicator){
                spawnedMob.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_AXE.getDefaultInstance());
            }

            if(spawnedMob instanceof Pillager){
                spawnedMob.setItemSlot(EquipmentSlot.MAINHAND, Items.CROSSBOW.getDefaultInstance());
            }

            if(spawnedMob instanceof Drowned){
                spawnedMob.setItemSlot(EquipmentSlot.MAINHAND, Items.TRIDENT.getDefaultInstance());
            }

            //System.out.println(Component.literal("Spawning new mob from portal"));
            spawnedMob.setPos(this.getX(), this.getY(), this.getZ());
            Player nearestPlayer = spawnedMob.level().getNearestPlayer(spawnedMob, 64.0D);
            if (spawnedMob instanceof Mob){
                ((Mob) spawnedMob).setTarget(nearestPlayer);
            }
            this.level().addFreshEntity(spawnedMob);
        }
    }

    private int getCooldown(double weight){
        return (int) (23-weight)*10;
    }

    private void teleportPlayerToPortal(ServerPlayer player, BlockPos portalPos, Level level) {
        player.teleportTo(portalPos.getX(), portalPos.getY(), portalPos.getZ());
        playPortalTravelSound(player);
    }

    public void setLinkedPortal(CursedPortalEntity linkedPortal) {
        this.linkedPortal = linkedPortal;
        if (linkedPortal != null) {
            this.entityData.set(LINKED_PORTAL_UUID, Optional.of(linkedPortal.getUUID()));
        } else {
            this.entityData.set(LINKED_PORTAL_UUID, Optional.empty());
        }
    }

    public CursedPortalEntity getLinkedPortal() {
        if (this.linkedPortal == null) {
            Optional<UUID> optionalUUID = this.entityData.get(LINKED_PORTAL_UUID);
            if (optionalUUID.isPresent() && this.level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(optionalUUID.get());
                if (entity instanceof CursedPortalEntity) {
                    this.linkedPortal = (CursedPortalEntity) entity;
                }
            }
        }
        return this.linkedPortal;
    }

    public static CursedPortalEntity spawnPortal(Level level, BlockPos altarPos, Object owner) {
        CursedPortalEntity portal = new CursedPortalEntity(ModEntities.CURSED_PORTAL.get(), level);
        portal.setOwner(owner);
        portal.setPos(altarPos.getX() + 0.5, altarPos.getY(), altarPos.getZ() + 0.5);
        portal.setTeleportEnabled(true);
        level.addFreshEntity(portal);
        return portal;
    }

    private void teleportPlayerToAltar(ServerPlayer player, BlockPos altarPos, Level level) {
        BlockPos playerPos = player.blockPosition();
        ServerLevel serverLevel = (ServerLevel) level;

        float yaw = calculateYawTowardAltar(playerPos, altarPos);
        float pitch = calculatePitchTowardAltar(player, playerPos, altarPos);

        player.teleportTo(serverLevel, altarPos.getX() + 2.5, altarPos.getY(), altarPos.getZ() + 2.5, Set.of(), yaw, pitch);
        playPortalTravelSound(player);
    }

    private static float calculateYawTowardAltar(BlockPos playerPos, BlockPos altarPos) {
        double deltaX = (altarPos.getX() + 0.5) - playerPos.getX();
        double deltaZ = (altarPos.getZ() + 0.5) - playerPos.getZ();
        return (float) (Math.atan2(deltaZ, deltaX) * (180.0 / Math.PI)) - 193.0F;
    }

    private static float calculatePitchTowardAltar(Player player, BlockPos playerPos, BlockPos altarPos) {
        double deltaX = (altarPos.getX() + 0.5) - playerPos.getX();
        double deltaZ = (altarPos.getZ() + 0.5) - playerPos.getZ();
        double deltaY = altarPos.getY() - (playerPos.getY() + player.getEyeHeight());

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        return (float) -(Math.atan2(deltaY, horizontalDistance) * (180.0 / Math.PI));
    }

    public void setTeleportEnabled(boolean enabled) {
        this.entityData.set(TELEPORT_ENABLED, enabled);
    }

    public boolean isTeleportEnabled() {
        return this.entityData.get(TELEPORT_ENABLED);
    }

    public void setSpawningEnabled(boolean enabled) {
        this.entityData.set(SPAWNING_ENABLED, enabled);
    }

    public boolean isSpawningEnabled() {
        return this.entityData.get(SPAWNING_ENABLED);
    }

    public int getTextureIndex() {
        return this.entityData.get(TEXTURE_INDEX);
    }

    public void setTextureIndex(int index) {
        this.entityData.set(TEXTURE_INDEX, index);
    }

    public int getMaxTextureIndex() {
        return 13;
    }

    public void setAltarPos(BlockPos pos) {
        this.altarPos = pos;
    }

    public static CursedPortalEntity spawnPortalNearPlayer(Player player, BlockPos altarPos, Level level, Object owner) {
        BlockPos portalPos = findRandomValidLocationNearPlayer(player, level);

        if (portalPos != null) {
            CursedPortalEntity portal = new CursedPortalEntity(ModEntities.CURSED_PORTAL.get(), level);
            portal.setOwner(owner);
            portal.setPos(portalPos.getX() + 0.5, portalPos.getY() + 0.5, portalPos.getZ() + 0.5);
            portal.setAltarPos(altarPos);
            portal.setTeleportEnabled(true);
            level.addFreshEntity(portal);
            return portal;
        } else {
            player.displayClientMessage(Component.literal("Portal cannot manifest!").withStyle(ChatFormatting.RED), true);
            return null;
        }
    }
    public static CursedPortalEntity spawnSummoningPortalOnPlayer(Player player, BlockPos altarPos, Level level, Object owner) {
        BlockPos portalPos = player.blockPosition();
        CursedPortalEntity portal = new CursedPortalEntity(ModEntities.CURSED_PORTAL.get(), level);
        portalLiveTime = 300;

        portal.setPos(portalPos.getX(), portalPos.getY(), portalPos.getZ());

        portal.setOwner(owner);
        portal.setAltarPos(altarPos);
        portal.setSpawningEnabled(true);
        //System.out.println(Component.literal("New summoning portal spawned!"));
        level.addFreshEntity(portal);
        return portal;
    }

    private static BlockPos findRandomValidLocationNearPlayer(Player player, Level level) {
        RandomSource random = level.getRandom();
        BlockPos playerPos = player.blockPosition();

        for (int attempts = 0; attempts < 10; attempts++) {
            int offsetX = random.nextInt(7) - 3;
            int offsetZ = random.nextInt(7) - 3;
            int offsetY = random.nextInt(3) - 1;

            BlockPos potentialPos = playerPos.offset(offsetX, offsetY, offsetZ);

            if (isValidPortalPosition(level, potentialPos) && isFarEnoughFromPlayer(playerPos, potentialPos)) {
                return potentialPos;
            }
        }
        return null;
    }

    private static boolean isValidPortalPosition(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
    }

    private static boolean isFarEnoughFromPlayer(BlockPos playerPos, BlockPos portalPos) {
        double distanceSquared = playerPos.distSqr(portalPos);
        return distanceSquared >= 4.0;
    }

    private void detectAndApplyScreenEffect() {
        double distanceThreshold = 2.5;
        List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(distanceThreshold));

        for (Player player : players) {
            if (player instanceof ServerPlayer serverPlayer&&!isSpawningEnabled()) {
                double distance = player.distanceTo(this);
                float alpha;
                if (distance <= 1.0) {
                    alpha = 0.1F;
                } else if (distance <= distanceThreshold) {
                    alpha = 0.1F * (float) (1.0 - (distance - 1.0) / (distanceThreshold - 1.0));
                } else {
                    alpha = 0.0F;
                }
                sendPortalScreenEffectPacket(serverPlayer, alpha);
            }
        }
    }


    private void sendPortalScreenEffectPacket(ServerPlayer player, float alpha) {
        //ModNetworking.sendToPlayer(new PortalOverlayPacketS2C(alpha), player);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.setTeleportEnabled(pCompound.getBoolean("TeleportEnabled"));
        this.setSpawningEnabled(pCompound.getBoolean("SpawningEnabled"));
        if (pCompound.contains("AltarPos")) {
            BlockPos pos = BlockPos.of(pCompound.getLong("AltarPos"));
            this.setAltarPos(pos);
        }
        if (pCompound.hasUUID("LinkedPortalUUID")) {
            UUID uuid = pCompound.getUUID("LinkedPortalUUID");
            this.entityData.set(LINKED_PORTAL_UUID, Optional.of(uuid));
        } else {
            this.entityData.set(LINKED_PORTAL_UUID, Optional.empty());
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putBoolean("TeleportEnabled", this.isTeleportEnabled());
        if (this.altarPos != null) {
            pCompound.putLong("AltarPos", this.altarPos.asLong());
        }
        Optional<UUID> linkedPortalUUID = this.entityData.get(LINKED_PORTAL_UUID);
        linkedPortalUUID.ifPresent(uuid -> pCompound.putUUID("LinkedPortalUUID", uuid));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
