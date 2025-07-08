package dev.toapuro.advancedkjs;

import dev.toapuro.advancedkjs.coremod.CoreModUtil;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AdvancedKJS.MODID)
public class AdvancedKJS
{
    public static final String MODID = "advancedkjs";

    public AdvancedKJS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onLoadComplete);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG.getSpec());
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        CoreModUtil.runAgent();
    }
}
