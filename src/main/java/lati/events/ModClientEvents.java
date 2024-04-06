package lati.events;

import lati.LatisTools;
import lati.events.screens.ModTableScreen;
import lati.events.screens.ToolTableScreen;
import lati.init.MenuInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LatisTools.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.MOD_TABLE.get(), ModTableScreen::new);
        MenuScreens.register(MenuInit.TOOL_TABLE.get(), ToolTableScreen::new);
    }
}
