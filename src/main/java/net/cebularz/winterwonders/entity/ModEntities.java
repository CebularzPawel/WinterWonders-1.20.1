package net.cebularz.winterwonders.entity;

import net.cebularz.winterwonders.entity.custom.*;
import net.cebularz.winterwonders.entity.custom.projectile.ChillingSnowballEntity;
import net.cebularz.winterwonders.entity.custom.projectile.IceCubeEntity;
import net.cebularz.winterwonders.entity.custom.projectile.OrnamentEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.projectile.IcicleProjectileEntity;

import java.util.function.Supplier;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WinterWonders.MOD_ID);

    public static final RegistryObject<EntityType<SnowWispEntity>> SNOW_WISP =
            TYPES.register("snow_wisp",()-> EntityType.Builder.of(SnowWispEntity::new, MobCategory.AMBIENT)
                    .sized(0.35F, 0.6F).clientTrackingRange(8).updateInterval(2).build("snow_wisp"));

    public static final RegistryObject<EntityType<RevenantEntity>> REVENANT =
            TYPES.register("revenant",()-> EntityType.Builder.of(RevenantEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("revenant"));

    public static final RegistryObject<EntityType<BriskEntity>> BRISK =
            TYPES.register("brisk",()-> EntityType.Builder.of(BriskEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).build("brisk"));

    public static final RegistryObject<EntityType<PinginEntity>> PINGIN =
            TYPES.register("pingin",()-> EntityType.Builder.of(PinginEntity::new, MobCategory.CREATURE)
                    .sized(0.4F, .9F).build("pingin"));

    public static final RegistryObject<EntityType<SnowWeaselEntity>> SNOW_WEASEL =
            TYPES.register("snow_weasel",()-> EntityType.Builder.of(SnowWeaselEntity::new, MobCategory.CREATURE)
                    .sized(0.4F, .4F).build("snow_weasel"));

    public static final RegistryObject<EntityType<MittenMouseEntity>> MITTEN_MOUSE =
            TYPES.register("mitten_mouse",()-> EntityType.Builder.of(MittenMouseEntity::new, MobCategory.CREATURE)
                    .sized(0.375f, 0.375f).build("mitten_mouse"));

    public static final RegistryObject<EntityType<LichEntity>> LICH =
            TYPES.register("lich",()-> EntityType.Builder.of(LichEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).build("lich"));

    public static final RegistryObject<EntityType<IcicleProjectileEntity>> ICICLE =
            TYPES.register("icicle",()-> EntityType.Builder.<IcicleProjectileEntity>of(IcicleProjectileEntity::new, MobCategory.MISC)
                    .sized(0.25F, .25F).build("icicle"));

    public static final RegistryObject<EntityType<ChillingSnowballEntity>> CHILLING_SNOWBALL =
            TYPES.register("chilling_snowball",()-> EntityType.Builder.<ChillingSnowballEntity>of(ChillingSnowballEntity::new, MobCategory.MISC)
                    .sized(0.25F, .25F).build("chilling_snowball"));

    public static final RegistryObject<EntityType<IceCubeEntity>> ICE_CUBE =
            TYPES.register("ice_cube", ()-> EntityType.Builder.<IceCubeEntity>of(IceCubeEntity::new, MobCategory.MISC)
                    .sized(1.0F,1.0F).noSummon().build("ice_cube"));

    public static final Supplier<EntityType<OrnamentEntity>> ORNAMENT =
            TYPES.register("ornament", () -> EntityType.Builder.of(OrnamentEntity::new, MobCategory.MISC)
                    .sized(0.3125f, 0.3125f).build("ornament"));

    public static void register(IEventBus eventBus)
    {
        TYPES.register(eventBus);
    }
}
