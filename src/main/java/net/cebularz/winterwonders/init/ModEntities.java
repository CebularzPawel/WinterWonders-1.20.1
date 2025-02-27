package net.cebularz.winterwonders.init;

import net.cebularz.winterwonders.entity.custom.projectile.ChillingSnowballEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.cebularz.winterwonders.WinterWonders;
import net.cebularz.winterwonders.entity.custom.BriskEntity;
import net.cebularz.winterwonders.entity.custom.PinginEntity;
import net.cebularz.winterwonders.entity.custom.RevenantEntity;
import net.cebularz.winterwonders.entity.custom.SnowWispEntity;
import net.cebularz.winterwonders.entity.custom.projectile.IceSpikeProjectileEntity;

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

    public static final RegistryObject<EntityType<IceSpikeProjectileEntity>> ICE_SPIKE =
            TYPES.register("ice_spike",()-> EntityType.Builder.<IceSpikeProjectileEntity>of(IceSpikeProjectileEntity::new, MobCategory.MISC)
                    .sized(0.25F, .25F).build("ice_spike"));

    public static final RegistryObject<EntityType<ChillingSnowballEntity>> CHILLING_SNOWBALL =
            TYPES.register("chilling_snowball",()-> EntityType.Builder.<ChillingSnowballEntity>of(ChillingSnowballEntity::new, MobCategory.MISC)
                    .sized(0.25F, .25F).build("chilling_snowball"));

    public static void register(IEventBus eventBus)
    {
        TYPES.register(eventBus);
    }
}
