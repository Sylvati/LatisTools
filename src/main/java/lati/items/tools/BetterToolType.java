package lati.items.tools;

import lati.LatisTools;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum BetterToolType {
    BETTER_PICKAXE_TYPE("better_pickaxe_type", () -> Ingredient.of(Items.DIAMOND));

    private final Tier toolTier;

    BetterToolType(String name, Supplier<Ingredient> repairIngredient) {
        this.toolTier = new Tier() {
            @Override
            public int getUses() {
                return Tiers.DIAMOND.getUses() * 3;
            }

            @Override
            public float getSpeed() {
                return Tiers.DIAMOND.getSpeed() * 3.0F;
            }

            @Override
            public float getAttackDamageBonus() {
                return Tiers.DIAMOND.getAttackDamageBonus();
            }

            @Override
            public int getLevel() {
                return Tiers.DIAMOND.getLevel();
            }

            @Override
            public int getEnchantmentValue() {
                return Tiers.DIAMOND.getEnchantmentValue();
            }

            @Override //TODO fix
            public Ingredient getRepairIngredient() {
                return repairIngredient.get();
            }

            @Override
            public String toString() {
                return LatisTools.MODID + ":" + name;
            }
        };
    }

    public final Tier getToolTier() {
        return toolTier;
    }
}
