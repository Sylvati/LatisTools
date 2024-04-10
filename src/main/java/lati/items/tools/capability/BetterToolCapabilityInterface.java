package lati.items.tools.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface BetterToolCapabilityInterface extends INBTSerializable<CompoundTag> {
    String getValue();

    void setMyValue(String myValue);
}
