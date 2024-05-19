package lati.init;

import lati.LatisTools;
import lati.items.tools.BetterTool;
import lati.items.tools.materials.VanillaMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LatisTools.MODID);

    //public static final RegistryObject<Item> BETTER_TOOL = ITEMS.register(BetterTool.getName(), () -> new BetterTool(props().stacksTo(1)));
    //replaced by below code



    public static void createUnfoundMaterialImages() {

    }

    public static void registerToolsWithMaterials() {
        for(var mat : VanillaMaterial.vanillaMaterials) {
            ITEMS.register(mat.getName() + "_" + BetterTool.getName(), () -> new BetterTool(props().stacksTo(1), mat.getTier()));
        }
    }

    private static Item.Properties props() {
        return new Item.Properties().tab(LatisTools.TAB);
    }
}
