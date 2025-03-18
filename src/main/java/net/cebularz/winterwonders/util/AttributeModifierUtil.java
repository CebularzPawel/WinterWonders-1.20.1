package net.cebularz.winterwonders.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AttributeModifierUtil {
    public static UUID generateUUIDFromName(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

    public static void applyPermanentModifier(LivingEntity livingEntity, Attribute attribute, String name, double value, AttributeModifier.Operation operation) {
        UUID uuid = generateUUIDFromName(name);
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            AttributeModifier existingModifier = attributeInstance.getModifier(uuid);
            if (existingModifier != null) {
                attributeInstance.removeModifier(existingModifier);
            }
            attributeInstance.addPermanentModifier(new AttributeModifier(uuid, name, value, operation));
        }
    }

    public static void applyTransientModifier(LivingEntity livingEntity, Attribute attribute, String name, double value, AttributeModifier.Operation operation) {
        UUID uuid = generateUUIDFromName(name);
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            AttributeModifier existingModifier = attributeInstance.getModifier(uuid);
            if (existingModifier != null) {
                attributeInstance.removeModifier(existingModifier);
            }
            attributeInstance.addTransientModifier(new AttributeModifier(uuid, name, value, operation));
        }
    }

    public static void removeModifier(LivingEntity livingEntity, Attribute attribute, String name) {
        UUID uuid = generateUUIDFromName(name);
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(uuid);
        }
    }

    public static void removeModifiersByPrefix(LivingEntity livingEntity, Attribute attribute, String prefix) {
        AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.getModifiers().stream()
                    .filter(modifier -> modifier.getName().startsWith(prefix))
                    .forEach(attributeInstance::removeModifier);
        }
    }

}
