package lati.blockentities;

import lati.LatisTools;
import lati.init.BlockEntityInit;
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

    public void tick() {
        if(level == null) {
            return;
        }

        //System.out.println("I ran");
        ItemStack slotZeroItem = this.inventory.getStackInSlot(0);
        CompoundTag slotZeroItemNbt = this.inventory.getStackInSlot(0).getTag();
        //System.out.println(this.inventory.getSlots());
        //System.out.println(slotZeroItem);
        //System.out.println(slotZeroItemNbt != null);


        if(slotZeroItemNbt != null) {
            //System.out.println(this.inventory.getStackInSlot(0).getTag().toString());
            if(slotZeroItemNbt.contains("available_modifiers")) {
                if(slotZeroItemNbt.getInt("available_modifiers") > 0) {
                    ItemStack slotOneItem = this.inventory.getStackInSlot(1);
                    ItemStack newItem = slotZeroItem.copy();
                    CompoundTag newItemNbt = newItem.getTag();
                    newItemNbt.putInt("available_modifiers", newItem.getTag().getInt("available_modifiers") - 1);
                    boolean recipeFound = false;

                    if (Items.QUARTZ.equals(slotOneItem.getItem())) {
                        if (newItemNbt.contains("sharpness")) {
                            if(newItemNbt.getInt("sharpness") < 5) {
                                newItemNbt.putInt("sharpness", newItemNbt.getInt("sharpness") + 1);
                                recipeFound = true;
                            }
                        } else {
                            newItemNbt.putInt("sharpness", 1);
                            recipeFound = true;
                        }

                    } else if (Items.REDSTONE.equals(slotOneItem.getItem())) {
                        if (newItemNbt.contains("speed")) {
                            if(newItemNbt.getInt("speed") < 3) {
                                newItemNbt.putInt("speed", newItemNbt.getInt("speed") + 1);
                                recipeFound = true;
                            }
                        } else {
                            newItemNbt.putInt("speed", 1);
                            recipeFound = true;
                        }
                    }

                    if(recipeFound) {
                        this.inventory.setStackInSlot(2, newItem);
                        this.inventory.setStackInSlot(0, ItemStack.EMPTY); //This solution sucks. There is a better way, I just don't know how
                    }
                }
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
