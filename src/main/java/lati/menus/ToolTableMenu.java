package lati.menus;

import lati.blockentities.ToolTableBlockEntity;
import lati.init.BlockInit;
import lati.init.MenuInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ToolTableMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess levelAccess;
    private final ContainerData data;
    public static final int TOOL_TABLE_TOTAL_SLOTS = 3;

    protected ToolTableMenu(int id, Inventory playerInv, IItemHandler slots, BlockPos pos, ContainerData data) {
        super(MenuInit.TOOL_TABLE.get(), id);
        this.levelAccess = ContainerLevelAccess.create(playerInv.player.getLevel(), pos);
        this.data = data;

        addSlot(new SlotItemHandler(slots, 0, 56, 35));
        addSlot(new SlotItemHandler(slots, 1, 86, 35));
        addSlot(new SlotWithRestriction(slots, 2, 116, 35));

        final int slotSizePlus2 = 18;
        final int startX = 8;
        final int startY = 84;
        final int hotbarY = 142;

        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                addSlot(new Slot(playerInv, column + row * 9 + 9, startX + column * slotSizePlus2, startY + row * slotSizePlus2));
            }
        }

        for(int column = 0; column < 9; ++column) {
            addSlot(new Slot(playerInv, column, startX + column * slotSizePlus2, hotbarY));
        }

        this.addDataSlots(this.data);
    }

    public static ToolTableMenu getClientMenu(int id, Inventory playerInv) {
        return new ToolTableMenu(id, playerInv, new ItemStackHandler(TOOL_TABLE_TOTAL_SLOTS), BlockPos.ZERO, new SimpleContainerData(TOOL_TABLE_TOTAL_SLOTS));
    }

    public static MenuConstructor getServerMenu(ToolTableBlockEntity blockEntity, BlockPos pos) {
        return (id, playerInv, player) -> new ToolTableMenu(id, playerInv, blockEntity.getInventory(), pos, blockEntity.getContainerData());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemstack = current.copy();

            if(index < 2) {
                if(!this.moveItemStackTo(current, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if(!this.moveItemStackTo(current, 0, 2, false)) {
                return ItemStack.EMPTY;
            }

            if(current.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, BlockInit.TOOL_TABLE.get());
    }

    public ContainerData getData() {
        return data;
    }


}
