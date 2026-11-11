package net.cebularz.winterwonders.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.client.ThrowingSpearClient;
import net.cebularz.winterwonders.entity.custom.projectile.ThrowingSpearProjectile;
import net.cebularz.winterwonders.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ThrowingSpearItem extends TridentItem {
    public static final int FULL_CHARGE_TICKS = 60;
    private static final ResourceKey<DamageType> DAMAGE_TYPE = ResourceKey.create(Registries.DAMAGE_TYPE,
            new ResourceLocation(WinterWonders.MOD_ID, "throwing_spear"));
    private static final float MIN_REACH = 2;
    private static final float MAX_REACH = 4.5F;
    private static final float CREATIVE_REACH = 6.5F;
    private static final float MOB_REACH = .5F;
    private static final float HIT_MARGIN = .125F;
    private final Tier material;
    private final int swingTicks;
    private final Multimap<Attribute, AttributeModifier> attributes;

    public ThrowingSpearItem(Tier material, float attackDuration, Properties properties) {
        super(properties.durability(material.getUses()));
        this.material = material;
        swingTicks = (int) (attackDuration * 20);
        attributes = ImmutableMultimap.of(
                Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        material.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION),
                Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                        1F / attackDuration - 4, AttributeModifier.Operation.ADDITION));
    }

    public int getSwingTicks() {
        return swingTicks;
    }

    public double getReach(LivingEntity attacker) {
        if (attacker instanceof Player player) {
            return player.isCreative() ? CREATIVE_REACH : MAX_REACH;
        } else {
            return MAX_REACH * MOB_REACH;
        }
    }

    public boolean canJab(Player player) {
        return player.isAlive() && !player.isSpectator() && !player.isUsingItem() && player.getAttackStrengthScale(0) >= 1;
    }

    public void jab(LivingEntity attacker, EquipmentSlot hand) {
        if (!attacker.level().isClientSide) {
            float damage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
            boolean hit = false;
            for (Entity target : getTargets(attacker)) {
                hit |= hurtTarget(attacker, hand, target, damage);
            }
            if (attacker instanceof Player player) {
                player.resetAttackStrengthTicker();
            }
            if (hit) {
                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                        ModSounds.THROWING_SPEAR_HIT.get(), attacker.getSoundSource(), 1, 1);
            }
            attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    ModSounds.THROWING_SPEAR_ATTACK.get(), attacker.getSoundSource(), 1, 1);
            attacker.swing(InteractionHand.MAIN_HAND, false);
        }
    }

    protected List<Entity> getTargets(LivingEntity attacker) {
        double min = attacker instanceof Player ? MIN_REACH : MIN_REACH * MOB_REACH;
        double max = getReach(attacker);
        Vec3 look = attacker.getViewVector(1);
        Vec3 eye = attacker.getEyePosition();
        Vec3 from = eye.add(look.scale(min));
        double movement = attacker.getDeltaMovement().dot(look);
        Vec3 to = eye.add(look.scale(max + Math.max(0, movement)));
        HitResult block = attacker.level().clip(new ClipContext(eye, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, attacker));
        if (block.getType() != HitResult.Type.MISS) {
            to = block.getLocation();
        }
        List<Entity> hits = new ArrayList<>();
        if (block.getType() == HitResult.Type.MISS || eye.distanceToSqr(to) >= eye.distanceToSqr(from)) {
            AABB search = AABB.ofSize(from, HIT_MARGIN, HIT_MARGIN, HIT_MARGIN).expandTowards(to.subtract(from)).inflate(1);
            for (Entity target : attacker.level().getEntities(attacker, search, entity -> canHit(attacker, entity))) {
                AABB bounds = target.getBoundingBox();
                if (bounds.contains(from)) {
                    hits.add(target);
                } else {
                    var exact = bounds.clip(from, to);
                    if (exact.isPresent()) {
                        hits.add(target);
                    } else {
                        var outside = bounds.inflate(HIT_MARGIN).clip(from, to);
                        if (outside.isPresent()) {
                            Vec3 edge = outside.get();
                            HitResult wall = attacker.level().clip(new ClipContext(edge, bounds.getCenter(),
                                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, attacker));
                            if (bounds.clip(edge, wall.getLocation()).isPresent()) {
                                hits.add(target);
                            }
                        }
                    }
                }
            }
        }
        return hits;
    }

    protected boolean canHit(LivingEntity attacker, Entity target) {
        if (target.isAlive() && target.isPickable() && !target.isSpectator()) {
            if (target instanceof Player other && attacker instanceof Player player) {
                return player.canHarmPlayer(other) && attacker.getRootVehicle() != target.getRootVehicle();
            } else {
                return attacker.getRootVehicle() != target.getRootVehicle();
            }
        } else {
            return false;
        }
    }

    protected boolean hurtTarget(LivingEntity attacker, EquipmentSlot hand, Entity target, float damage) {
        Player player = attacker instanceof Player user ? user : null;
        if (attacker.level() instanceof ServerLevel level && target.isAttackable()
                && (player == null || !target.skipAttackInteraction(player))) {
            ItemStack stack = attacker.getItemBySlot(hand);
            DamageSource source = new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DAMAGE_TYPE), attacker);
            float bonus = EnchantmentHelper.getDamageBonus(stack, target instanceof LivingEntity living ? living.getMobType() : MobType.UNDEFINED);
            if (player != null) {
                float strength = player.getAttackStrengthScale(.5F);
                damage *= .2F + strength * strength * .8F;
                bonus *= strength;
            }
            float health = target instanceof LivingEntity living ? living.getHealth() : 0;
            boolean hurt = target.hurt(source, damage + bonus);
            knockback(attacker, target, stack);
            if (player == null && target instanceof LivingEntity living) {
                hurtEnemy(stack, living, attacker);
            }
            if (player != null) {
                player.setLastHurtMob(target);
            }
            if (hurt) {
                if (target instanceof LivingEntity living) {
                    EnchantmentHelper.doPostHurtEffects(living, attacker);
                }
                EnchantmentHelper.doPostDamageEffects(attacker, target);
            }
            if (player != null) {
                if (hurt) {
                    int fire = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
                    if (fire > 0) {
                        target.setSecondsOnFire(fire * 4);
                    }
                }
                if (target instanceof LivingEntity living) {
                    hurtEnemy(stack, living, player);
                }
                player.awardStat(Stats.ITEM_USED.get(this));
                player.causeFoodExhaustion(.1F);
                if (hurt && target instanceof LivingEntity living) {
                    float dealt = health - living.getHealth();
                    player.awardStat(Stats.DAMAGE_DEALT, Math.round(dealt * 10));
                    if (dealt > 2) {
                        level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getY(.5), target.getZ(),
                                (int) (dealt * .5), .1, 0, .1, .2);
                    }
                }
            } else {
                attacker.setLastHurtMob(target);
            }
            return true;
        } else {
            return false;
        }
    }

    protected void knockback(LivingEntity attacker, Entity target, ItemStack stack) {
        double yaw = Math.toRadians(attacker.getYRot());
        double power = .4 + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, stack) * .5;
        if (target instanceof LivingEntity living) {
            living.knockback(power, Math.sin(yaw), -Math.cos(yaw));
        } else {
            target.push(-Math.sin(yaw) * power, .1, Math.cos(yaw) * power);
        }
        if (attacker instanceof Player) {
            attacker.setDeltaMovement(attacker.getDeltaMovement().multiply(.6, 1, .6));
            attacker.setSprinting(false);
            if (target instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetEntityMotionPacket(target));
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return attributes;
        } else {
            return ImmutableMultimap.of();
        }
    }

    @Override
    public int getEnchantmentValue() {
        return material.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return material.getRepairIngredient().test(ingredient);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) {
        if (entity instanceof Player player && !level.isClientSide && stack.getDamageValue() < stack.getMaxDamage() - 1) {
            int held = getUseDuration(stack) - remaining;
            float charge = Mth.clamp(held / (float) FULL_CHARGE_TICKS, 0, 1);
            stack.hurtAndBreak(1, player, user -> user.broadcastBreakEvent(entity.getUsedItemHand()));
            ItemStack thrown = stack.copy();
            thrown.setCount(1);
            ThrowingSpearProjectile spear = new ThrowingSpearProjectile(level, player, thrown);
            spear.setThrowCharge(charge);
            spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, Mth.lerp(charge, .75F, SHOOT_POWER), 1);
            if (player.getAbilities().instabuild) {
                spear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            if (level.addFreshEntity(spear)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, spear, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1, 1);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.IMPALING || enchantment == Enchantments.LOYALTY
                || enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING
                || enchantment == Enchantments.VANISHING_CURSE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new ThrowingSpearClient());
    }
}
