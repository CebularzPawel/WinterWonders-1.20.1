package net.turtleboi.winterwonders.entity.custom.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class DamaginSnowBall extends Snowball {
    public DamaginSnowBall(EntityType<? extends Snowball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


}
