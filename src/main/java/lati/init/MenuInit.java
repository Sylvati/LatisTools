package lati.init;

import lati.LatisTools;
import lati.menus.ModTableMenu;
import lati.menus.ToolTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, LatisTools.MODID);

    public static final RegistryObject<MenuType<ModTableMenu>> MOD_TABLE = MENUS.register("mod_table", () -> new MenuType<>(ModTableMenu::getClientMenu));
    public static final RegistryObject<MenuType<ToolTableMenu>> TOOL_TABLE = MENUS.register("tool_table", () -> new MenuType<>(ToolTableMenu::getClientMenu));
}
