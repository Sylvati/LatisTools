package lati.blockentities;

import lati.LatisTools;
import lati.init.BlockEntityInit;
import lati.items.tools.modifiers.Modifier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static lati.items.tools.ToolIDs.*;
import static lati.menus.ToolTableMenu.TOOL_TABLE_TOTAL_SLOTS;

public class ToolTableBlockEntity extends BlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(TOOL_TABLE_TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            ToolTableBlockEntity.this.setChanged();
            super.onContentsChanged(slot);
        }
    };

    private final LazyOptional<IItemHandlerModifiable> optional = LazyOptional.of(() -> this.inventory);

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return 0;
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 0;
        }
    };

    public static final Component TITLE = Component.translatable("container." + LatisTools.MODID + ".tool_table");

    public ToolTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.TOOL_TABLE.get(), pos, state);
    }

    public void tick() { //Dude, this should NOT be running every tick. This is a lazy solution on my part, but it'll do until I fix it (lol)
        if(level == null) {
            return;
        }

        //System.out.println("I ran");
        ItemStack slotZeroItem = this.inventory.getStackInSlot(0);
        CompoundTag slotZeroItemNbt = this.inventory.getStackInSlot(0).getTag();
        CompoundTag slotZeroItemModifierNbt;

        if(slotZeroItemNbt == null) { return; } //Protection clause
        slotZeroItemModifierNbt = (CompoundTag)slotZeroItemNbt.get(MODIFIERS_DATA_REF);
        //System.out.println(this.inventory.getSlots());
        //System.out.println(slotZeroItem);
        //System.out.println(slotZeroItemNbt != null);


        //System.out.println(this.inventory.getStackInSlot(0).getTag().toString());
        if(slotZeroItemModifierNbt.getInt(AVAILABLE_MODIFIERS_REF) > 0) {
            ItemStack slotOneItem = this.inventory.getStackInSlot(1);
            ItemStack newItem = slotZeroItem.copy();
            CompoundTag newItemNbt = newItem.getTag();
            CompoundTag newItemModifierNbt = (CompoundTag)newItemNbt.get(MODIFIERS_DATA_REF);
            newItemModifierNbt.putInt(AVAILABLE_MODIFIERS_REF, newItemModifierNbt.getInt(AVAILABLE_MODIFIERS_REF) - 1);
            CompoundTag newItemModifiers = (CompoundTag)newItemModifierNbt.get(MODIFIERS_REF);
            boolean recipeFound = false;

            //Do this later to support one item adding multiple modifiers for one resource. Why? cus its cool. For now im being lazy tho lol
            //Map<Item, List<Modifier>> valid_items = Map.of(Items.QUARTZ, Arrays.asList(Modifier.SHARPNESS_MODIFIER),
            //        Items.REDSTONE, Arrays.asList(Modifier.SPEED_MODIFIER));

            Map<Item, Modifier> valid_items = Map.of(Items.QUARTZ, Modifier.SHARPNESS_MODIFIER,
                    Items.REDSTONE, Modifier.SPEED_MODIFIER);

            for(var entry: valid_items.entrySet()) {
                Item item = entry.getKey();
                if(slotOneItem.getItem().equals(item)) { //Check if there's a valid material in the middle.

                    Modifier curr_mod = entry.getValue();

                    if(newItemModifiers.contains(valid_items.get(item).getCodeName())) { //Check if we already have the modifier.
                        if(newItemModifiers.getInt(curr_mod.getCodeName()) + curr_mod.getIncrement() <= curr_mod.getMaxLevel()) { //Make sure we aren't above the max level
                            newItemModifiers.putInt(curr_mod.getCodeName(), newItemModifiers.getInt(curr_mod.getCodeName()) + curr_mod.getIncrement());
                            recipeFound = true;
                        }
                    } else { //In the case that we do not have the modifier
                        newItemModifiers.putInt(curr_mod.getCodeName(), curr_mod.getIncrement());
                        recipeFound = true;
                    }
                }
            }

            if(recipeFound) {
                this.inventory.setStackInSlot(2, newItem);
                this.inventory.setStackInSlot(0, ItemStack.EMPTY); //This solution sucks. There is a better way, I just don't know how
            }
        }
    }



    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        System.out.println("TOOL TABLE: " + nbt.getCompound("Inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Inventory", this.inventory.serializeNBT());
        System.out.println("TOOL TABLE: " + this.inventory.serializeNBT());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? this.optional.cast() : super.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
        this.optional.invalidate();
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ContainerData getContainerData() {
        return this.data;
    }
}
