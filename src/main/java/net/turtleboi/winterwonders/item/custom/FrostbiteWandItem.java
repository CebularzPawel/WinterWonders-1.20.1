package net.turtleboi.winterwonders.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.turtleboi.winterwonders.entity.custom.projectile.IceSpikeProjectileEntity;

public class FrostbiteWandItem extends Item {
    public FrostbiteWandItem(Properties pProperties) {
        super(pProperties.durability(128));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);

        pPlayer.getCooldowns().addCooldown(this, 40);

        itemStack.hurtAndBreak(1, pPlayer,
                (player) -> player.broadcastBreakEvent(pUsedHand)
        );

        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 0.75f, 0.4f / (pLevel.getRandom().nextFloat() * 0.4f + 0.8f));

        if(!pLevel.isClientSide){
            IceSpikeProjectileEntity iceSpike = new IceSpikeProjectileEntity(pLevel, pPlayer);
            iceSpike.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0f, 2f, 1.0f);
            pLevel.addFreshEntity(iceSpike);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }
}

