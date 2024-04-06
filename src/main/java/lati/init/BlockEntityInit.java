package lati.init;

import lati.LatisTools;
import lati.blockentities.ModTableBlockEntity;
import lati.blockentities.ToolTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.world.level.block.entity.BlockEntityType.Builder.of;

public class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LatisTools.MODID);

    public static final RegistryObject<BlockEntityType<ModTableBlockEntity>> MOD_TABLE = BLOCK_ENTITIES.register("mod_table", () -> of(ModTableBlockEntity::new, BlockInit.MOD_TABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ToolTableBlockEntity>> TOOL_TABLE = BLOCK_ENTITIES.register("tool_table", () -> of(ToolTableBlockEntity::new, BlockInit.TOOL_TABLE.get()).build(null));
}
