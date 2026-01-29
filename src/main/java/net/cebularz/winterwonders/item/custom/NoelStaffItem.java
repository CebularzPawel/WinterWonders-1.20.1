package net.cebularz.winterwonders.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.cebularz.winterwonders.entity.ModEntities;
import net.cebularz.winterwonders.entity.custom.projectile.OrnamentEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NoelStaffItem extends Item {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("b0d5d8a9-8d19-4f34-8d5e-6d7702a8b3f1");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("7f6f0a6a-1e66-4d1b-9a3f-0c8e7a4639e4");
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public NoelStaffItem(Properties properties) {
        super(properties);
        this.defaultModifiers = ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(ATTACK_DAMAGE_UUID, "Weapon modifier", 5.0D, AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED,
                        new AttributeModifier(ATTACK_SPEED_UUID, "Weapon modifier", 1.6D, AttributeModifier.Operation.ADDITION))
                .build();
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        ItemStack staffStack = pPlayer.getItemInHand(hand);
        if (level.isClientSide) {
            pPlayer.swing(hand, true);
            return InteractionResultHolder.success(staffStack);
        }

        Vec3 look = pPlayer.getLookAngle();
        Vec3 spawnPos = pPlayer.getEyePosition().add(look.scale(0.35));
        OrnamentEntity ornament = new OrnamentEntity(ModEntities.ORNAMENT.get(), level);
        ornament.setOwner(pPlayer);
        ornament.setVariant(OrnamentEntity.OrnamentVariant.getRandomVariant(level.random));
        ornament.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, pPlayer.getYRot(), pPlayer.getXRot());

        float speed = 1.33f;
        float inaccuracy = 0.5f;
        ornament.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, speed, inaccuracy);
        level.addFreshEntity(ornament);
        RandomSource random = level.getRandom();
        float volume = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.15f;
        float pitch = 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f;
        level.playSound(
                null,
                pPlayer.blockPosition(),
                SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.PLAYERS,
                volume,
                pitch
        );

        pPlayer.getCooldowns().addCooldown(this, 20);
        staffStack.hurtAndBreak(1, pPlayer, player -> player.broadcastBreakEvent(hand));
        return InteractionResultHolder.consume(staffStack);
    }
}
