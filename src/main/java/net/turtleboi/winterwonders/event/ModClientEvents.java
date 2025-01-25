package net.turtleboi.winterwonders.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.turtleboi.winterwonders.WinterWonders;
import net.turtleboi.winterwonders.client.PlayerClientData;
import net.turtleboi.winterwonders.effect.ModEffects;

import java.util.Random;

@Mod.EventBusSubscriber(modid = WinterWonders.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

}
