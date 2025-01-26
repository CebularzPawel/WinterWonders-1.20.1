package net.turtleboi.winterwonders.entity.custom;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.turtleboi.winterwonders.entity.ai.SnowWispAI;
import net.turtleboi.winterwonders.init.ModParticles;
import org.jetbrains.annotations.NotNull;

public class SnowWisp extends Monster {

    public SnowWisp(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED,0.23D)
                .add(Attributes.MAX_HEALTH, 12D)
                .add(Attributes.FLYING_SPEED, 0.25D);
    }

    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    //This is so that it would be passive but spawn at night
    @Override
    public boolean doHurtTarget(@NotNull Entity pEntity) {
        return !(pEntity instanceof Player);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity pTarget) {
        return !(pTarget instanceof Player);
    }

    @Override
    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> pDynamic) {
        return SnowWispAI.makeBrain(this,pDynamic);
    }

    @Override
    public Brain<SnowWisp> getBrain() {
        return (Brain<SnowWisp>) super.getBrain();
    }

    protected void customServerAiStep() {
        ServerLevel level = (ServerLevel)this.level();
        level.getProfiler().push("wispBrain");
        this.getBrain().tick(level, this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("wispUpdateActivity");
        SnowWispAI.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {

    }

    @Override
    protected boolean isFlapping() {
        return !this.onGround();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.onGround()) {
            double entityX = getX();
            double entityY = getY() - 0.5;
            double entityZ = getZ();

            double spreadX = (random.nextDouble() - 0.5) * 0.4;
            double spreadZ = (random.nextDouble() - 0.5) * 0.4;

            level().addParticle(
                    ModParticles.AURORA_PARTICLE.get(),
                    true,
                    entityX + spreadX,
                    entityY,
                    entityZ + spreadZ,
                    0, -0.05, 0
            );
        }
    }
}
