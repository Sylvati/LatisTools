package lati.init;

import lati.LatisTools;

import lati.blocks.ModTableBlock;
import lati.blocks.ToolTableBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LatisTools.MODID);

    public static final RegistryObject<Block> MOD_TABLE = register("mod_table", () -> new ModTableBlock(
            BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(5f, 20f)), new Item.Properties().tab(LatisTools.TAB));

    public static final RegistryObject<Block> TOOL_TABLE = register("tool_table", () -> new ToolTableBlock(
            BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(5f, 20f)), new Item.Properties().tab(LatisTools.TAB));




    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> supplier, Item.Properties properties) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
        return block;
    }
}
