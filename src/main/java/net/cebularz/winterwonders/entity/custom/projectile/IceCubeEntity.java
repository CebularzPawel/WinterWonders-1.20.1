package net.cebularz.winterwonders.entity.custom.projectile;

import net.cebularz.winterwonders.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.turtleboi.turtlecore.effect.CoreEffects;

public class IceCubeEntity extends ThrowableProjectile
{
    public IceCubeEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity target = pResult.getEntity();
        if(target instanceof LivingEntity lv)
        {
            lv.addEffect(new MobEffectInstance(CoreEffects.CHILLED.get(),100));
            lv.hurt(level().damageSources().explosion(this,target), 1.2F);
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        BlockPos hitPos = pResult.getBlockPos();
        level().setBlock(hitPos, Blocks.ICE.defaultBlockState(),3);
        this.discard();
    }
}
