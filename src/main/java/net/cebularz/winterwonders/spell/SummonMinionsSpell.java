package net.cebularz.winterwonders.spell;

import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.entity.custom.LichEntity;
import net.cebularz.winterwonders.entity.custom.RevenantEntity;
import net.cebularz.winterwonders.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.turtleboi.turtlecore.spell.Spell;

public class SummonMinionsSpell implements Spell {
    private final int minCount;
    private final int maxExtra;
    private final double spawnRadius;
    private final boolean retargetToTarget;

    public SummonMinionsSpell(int minCount, int maxExtra, double spawnRadius, boolean retargetToTarget) {
        this.minCount = Math.max(1, minCount);
        this.maxExtra = Math.max(0, maxExtra);
        this.spawnRadius = Math.max(0.5, spawnRadius);
        this.retargetToTarget = retargetToTarget;
    }

    @Override
    public void cast(ServerLevel level, LivingEntity caster, LivingEntity target, RandomSource random) {
        if (level == null || caster == null || random == null) return;
        if (!caster.isAlive()) return;

        if (target != null && (!target.isAlive() || target.isAlliedTo(caster))) {
            target = null;
        }

        int count = minCount + (maxExtra == 0 ? 0 : random.nextInt(maxExtra + 1));

        for (int i = 0; i < count; i++) {
            Mob minion = createMinion(level, caster, random);
            double ox = (random.nextDouble() - 0.5) * (spawnRadius * 2.0);
            double oz = (random.nextDouble() - 0.5) * (spawnRadius * 2.0);

            minion.moveTo(caster.getX() + ox, caster.getY(), caster.getZ() + oz, caster.getYRot(), 0.0F);
            minion.setCustomNameVisible(false);
            level.addFreshEntity(minion);

            if (caster instanceof LichEntity lichEntity) {
                lichEntity.spawnedMinions.add(minion);
            }

            if (retargetToTarget && target != null && target.isAlive()) {
                minion.setTarget(target);
            }


            level.playSound(
                    null,
                    minion.blockPosition(),
                    SoundEvents.PLAYER_HURT_FREEZE,
                    SoundSource.HOSTILE,
                    1f,
                    1f
            );

            for (int p = 0; p < 40; p++) {
                double px = (random.nextDouble() - 0.5);
                double py = random.nextDouble() * 1.95;
                double pz = (random.nextDouble() - 0.5);

                level.sendParticles(
                        ParticleTypes.SNOWFLAKE,
                        minion.getX() + px,
                        minion.getY() + py,
                        minion.getZ() + pz,
                        1,
                        0, 0, 0,
                        0.01
                );
            }
        }

        if (caster instanceof LichEntity lichEntity) {
            lichEntity.spawnedMinions.removeIf(mob -> mob == null || mob.isRemoved() || !mob.isAlive());
        }
    }

    private static Mob createMinion(ServerLevel level, LivingEntity caster, RandomSource random) {
        if (random.nextBoolean()) {
            Stray strayEntity = new Stray(EntityType.STRAY, level);
            strayEntity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
            strayEntity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.COLDSTEEL_HELMET.get()));
            return strayEntity;
        }

        RevenantEntity revenantEntity = new RevenantEntity(ModEntities.REVENANT.get(), level);
        RevenantEntity.equipRandomColdsteelArmor(revenantEntity);
        RevenantEntity.equipRandomColdsteelWeapon(revenantEntity, 0);
        return revenantEntity;
    }
}
