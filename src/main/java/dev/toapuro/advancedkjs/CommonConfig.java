package dev.toapuro.advancedkjs;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static CommonConfig CONFIG = new CommonConfig();

    private final ForgeConfigSpec spec;

    public CommonConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        spec = builder.build();
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }
}
