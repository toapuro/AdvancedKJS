package dev.toapuro.kubeextra;

import dev.toapuro.kubeextra.coremod.CoreModUtil;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(KubeExtra.MODID)
public class KubeExtra
{
    public static final String MODID = "kubeextra";
    private static final Logger LOGGER = LoggerFactory.getLogger(KubeExtra.class);

    public KubeExtra() {
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
