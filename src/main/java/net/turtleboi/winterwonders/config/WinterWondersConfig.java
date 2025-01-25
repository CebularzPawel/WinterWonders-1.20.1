package net.turtleboi.winterwonders.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class WinterWondersConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Double> CURSED_ALTAR_SPAWN_CHANCE;

    public static final ForgeConfigSpec.ConfigValue<Double> CURSED_TRIAL_TIER1_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> CURSED_TRIAL_TIER2_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Double> CURSED_TRIAL_TIER3_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> CURSED_TRIAL_MAX;

    public static final ForgeConfigSpec.ConfigValue<Integer> CURSE_TIME_MIN;
    public static final ForgeConfigSpec.ConfigValue<Integer> CURSE_TIME_MAX;

    static {
        BUILDER.push("Structures");

        CURSED_ALTAR_SPAWN_CHANCE = BUILDER
                .comment("Spawn chance for the Cursed Altar structure")
                .defineInRange("cursedAltarSpawnChance", 1.0, 0.0, 1.0);

        BUILDER.pop();

        BUILDER.push("Trial Modifiers");

        CURSED_TRIAL_TIER1_CHANCE = BUILDER
                .comment("Base chance for player to be cursed with a Tier 1 trial")
                .comment("All values for trials chances should ideally add up to 10 to avoid bugs")
                .defineInRange("cursedTrialTier1Chance", 6.0, 0.0, 10.0);

        CURSED_TRIAL_TIER2_CHANCE = BUILDER
                .comment("Base chance for player to be cursed with a Tier 2 trial")
                .comment("All values for trials chances should ideally add up to 10 to avoid bugs")
                .defineInRange("cursedTrialTier2Chance", 3.0, 0.0, 10.0);

        CURSED_TRIAL_TIER3_CHANCE = BUILDER
                .comment("Base chance for player to be cursed with a Tier 3 trial")
                .comment("All values for trials chances should ideally add up to 10 to avoid bugs")
                .defineInRange("cursedTrialTier3Chance", 1.0, 0.0, 10.0);

        CURSED_TRIAL_MAX = BUILDER
                .comment("Max amount of trials for player to complete before Tier 3 trials are guaranteed")
                .defineInRange("cursedTrialMaxChance", 25, 1, 256);

        BUILDER.pop();

        BUILDER.push("Curse Modifiers");

        CURSE_TIME_MIN = BUILDER
                .comment("Minimum number of seconds that a Survival Trial can curse the player for")
                .comment("If value is set to 0, all Survival Trials will result in an instant completion")
                .defineInRange("curseTimeMinimum", 180, 0, 12000);

        CURSE_TIME_MAX = BUILDER
                .comment("Maximum number of seconds that a Survival Trial can curse the player for")
                .comment("If value is set to 0, all Survival Trials will result in an instant completion")
                .defineInRange("curseTimeMaximum", 240, 0, 18000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
