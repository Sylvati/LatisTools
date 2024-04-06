package lati.init;

import lati.LatisTools;
import lati.items.tools.BetterTool;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LatisTools.MODID);

    public static final RegistryObject<Item> BETTER_TOOL = ITEMS.register(BetterTool.getName(), () -> new BetterTool(props().stacksTo(1)));

    private static Item.Properties props() {
        return new Item.Properties().tab(LatisTools.TAB);
    }
}
