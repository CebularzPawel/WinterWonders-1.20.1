package net.turtleboi.winterwonders.util;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AttributeModifierUtil {
    public static UUID generateUUIDFromName(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
    }

    public static void applyPermanentModifier(Player player, Attribute attribute, String name, double value, AttributeModifier.Operation operation) {
        UUID uuid = generateUUIDFromName(name);
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            if (attributeInstance.getModifier(uuid) != null) {
                attributeInstance.removeModifier(uuid);
            }
            attributeInstance.addPermanentModifier(new AttributeModifier(uuid, name, value, operation));
        }
    }

    public static void applyTransientModifier(Player player, Attribute attribute, String name, double value, AttributeModifier.Operation operation) {
        UUID uuid = generateUUIDFromName(name);
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            if (attributeInstance.getModifier(uuid) != null) {
                attributeInstance.removeModifier(uuid);
            }
            attributeInstance.addTransientModifier(new AttributeModifier(uuid, name, value, operation));
        }
    }

    public static void removeModifier(Player player, Attribute attribute, String name) {
        UUID uuid = generateUUIDFromName(name);
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(uuid);
        }
    }

    public static void removeModifiersByPrefix(Player player, Attribute attribute, String prefix) {
        AttributeInstance attributeInstance = player.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.getModifiers().stream()
                    .filter(modifier -> modifier.getName().startsWith(prefix))
                    .forEach(attributeInstance::removeModifier);
        }
    }

}
