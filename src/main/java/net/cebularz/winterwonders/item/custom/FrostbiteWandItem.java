package net.cebularz.winterwonders.item.custom;

import net.cebularz.winterwonders.enchantment.ModEnchantments;
import net.cebularz.winterwonders.entity.custom.projectile.ChillingSnowballEntity;
import net.cebularz.winterwonders.entity.custom.projectile.IcicleProjectileEntity;
import net.cebularz.winterwonders.spell.player.PlayerSpellbook;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.turtleboi.turtlecore.effect.CoreEffects;
import net.turtleboi.turtlecore.enchantment.CoreEnchantments;
import net.turtleboi.turtlecore.spell.Spell;
import net.turtleboi.turtlecore.util.TargetingUtils;
import org.apache.logging.log4j.core.Core;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FrostbiteWandItem extends Item {
    public FrostbiteWandItem(Properties pProperties) {
        super(pProperties.durability(128));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.sidedSuccess(itemInHand, true);
        }

        if (!(pLevel instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.fail(itemInHand);
        }

        if (pPlayer.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(itemInHand);
        }

        LivingEntity target = TargetingUtils.getLockedTarget(pPlayer);
        Spell chosenSpell = pickSpellForWand(pPlayer, itemInHand);
        PlayerSpellbook.cast(serverLevel, pPlayer, target, chosenSpell);
        pPlayer.getCooldowns().addCooldown(this, 40);
        itemInHand.hurtAndBreak(1, pPlayer, player -> player.broadcastBreakEvent(pUsedHand));

        pLevel.playSound(
                null,
                pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.NEUTRAL,
                0.75f,
                0.4f / (pLevel.getRandom().nextFloat() * 0.4f + 0.8f)
        );

        return InteractionResultHolder.success(itemInHand);
    }

    private static Spell pickSpellForWand(Player player, ItemStack wandItem) {
        boolean castIcicle = player.isShiftKeyDown();
        int barrageLevel = wandItem.getEnchantmentLevel(CoreEnchantments.BARRAGE.get());
        int seekingLevel = wandItem.getEnchantmentLevel(CoreEnchantments.SEEKING.get());
        boolean homing = seekingLevel > 0;

        int barrage = Math.max(0, Math.min(3, barrageLevel));
        if (!castIcicle) {
            return switch (barrage) {
                case 0 -> PlayerSpellbook.SNOWBALL;
                case 1 -> PlayerSpellbook.SNOWBALL_BARRAGE1;
                case 2 -> PlayerSpellbook.SNOWBALL_BARRAGE2;
                default -> PlayerSpellbook.SNOWBALL_BARRAGE3;
            };
        }

        if (!homing) {
            return switch (barrage) {
                case 0 -> PlayerSpellbook.ICICLE;
                case 1 -> PlayerSpellbook.ICICLE_BARRAGE1;
                case 2 -> PlayerSpellbook.ICICLE_BARRAGE2;
                default -> PlayerSpellbook.ICICLE_BARRAGE3;
            };
        } else {
            return switch (barrage) {
                case 0 -> PlayerSpellbook.ICICLE_HOMING;
                case 1 -> PlayerSpellbook.ICICLE_HOMING_BARRAGE1;
                case 2 -> PlayerSpellbook.ICICLE_HOMING_BARRAGE2;
                default -> PlayerSpellbook.ICICLE_HOMING_BARRAGE3;
            };
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Right-click: Chilling Snowball").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.literal("Shift + Right-click: Icicle").withStyle(ChatFormatting.GRAY));

        if (Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.empty());

            pTooltipComponents.add(Component.literal("Chilling Snowball").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            pTooltipComponents.add(Component.literal("• Non-harmful ranged projectile").withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(Component.literal("• Applies ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Chilled").setStyle(styleFromEffect(CoreEffects.CHILLED.get())))
                    .append(Component.literal(" on hit").withStyle(ChatFormatting.GRAY))
            );
            pTooltipComponents.add(Component.literal("• More than 4 stacks of ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Chilled").setStyle(styleFromEffect(CoreEffects.CHILLED.get())))
                    .append(Component.literal(" gives the target").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(" Frozen").setStyle(styleFromEffect(CoreEffects.FROZEN.get())))
            );

            pTooltipComponents.add(Component.empty());

            pTooltipComponents.add(Component.literal("Icicle").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD));
            pTooltipComponents.add(Component.literal("• Fast, hard-hitting projectile").withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(Component.literal("• Damage scales vs ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Chilled").setStyle(styleFromEffect(CoreEffects.CHILLED.get())))
                    .append(Component.literal(" targets").withStyle(ChatFormatting.GRAY))
            );
            pTooltipComponents.add(Component.literal("• Massive damage vs ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Frozen").setStyle(styleFromEffect(CoreEffects.FROZEN.get())))
                    .append(Component.literal(" targets").withStyle(ChatFormatting.GRAY))
            );

            pTooltipComponents.add(Component.empty());
            pTooltipComponents.add(Component.literal("Cooldown: [2.0s]").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            pTooltipComponents.add(Component.literal("Hold SHIFT for details").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    public static Style styleFromEffect(MobEffect effect) {
        int color = effect.getColor();
        return Style.EMPTY.withColor(color);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == CoreEnchantments.BARRAGE.get()) return true;
        if (enchantment == CoreEnchantments.SEEKING.get()) return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }
}

