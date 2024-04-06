package lati.blockentities;

import lati.LatisTools;
import lati.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static lati.menus.ModTableMenu.MOD_TABLE_TOTAL_SLOTS;

public class ModTableBlockEntity extends BlockEntity {
    private static final int MAX_PROGRESS = 100;
    private int progress;

    private final ItemStackHandler inventory = new ItemStackHandler(MOD_TABLE_TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            ModTableBlockEntity.this.setChanged();
            super.onContentsChanged(slot);
        }
    };

    private final LazyOptional<IItemHandlerModifiable> optional = LazyOptional.of(() -> this.inventory);

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0 -> ModTableBlockEntity.this.progress;
                case 1 -> ModTableBlockEntity.MAX_PROGRESS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0 -> ModTableBlockEntity.this.progress = value;
                default -> {}
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public static final Component TITLE = Component.translatable("container." + LatisTools.MODID + ".mod_table");

    public ModTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.MOD_TABLE.get(), pos, state);
    }

    public void tick() {
        if(level == null) {
            return;
        }

        progress++;
        if(progress > MAX_PROGRESS) {
            progress = 0;
            var pig = new Pig(EntityType.PIG, this.level);
            pig.setPos(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 1.5D, this.worldPosition.getZ() + 0.5D);
            this.level.addFreshEntity(pig);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.progress = nbt.getInt("Progress");
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        System.out.println(nbt.getCompound("Inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("Progress", this.progress);
        nbt.put("Inventory", this.inventory.serializeNBT());
        System.out.println(this.inventory.serializeNBT());
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? this.optional.cast() : super.getCapability(cap, side);
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
