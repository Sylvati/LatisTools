package lati.items.tools.capability;

import net.minecraft.nbt.CompoundTag;

public class BetterToolCapabilityImplementation implements BetterToolCapabilityInterface{
    private static final String NBT_KEY_DAMAGE_DEALT = "damageDealt";

    private String myValue = "";

    @Override
    public String getValue() {
        return this.myValue;
    }

    @Override
    public void setMyValue(String myValue) {
        this.myValue = myValue;
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putString(NBT_KEY_DAMAGE_DEALT, this.myValue);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.myValue = nbt.getString(NBT_KEY_DAMAGE_DEALT);
    }
}
