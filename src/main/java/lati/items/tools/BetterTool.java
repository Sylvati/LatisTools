package lati.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lati.items.tools.modifiers.Modifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static lati.items.tools.ToolIDs.*;

public class BetterTool extends Item {
    private static final String name = "better_tool";

    private final TagKey<Block> blocks;
    private final float speed;
    private float attackDamageBase;
    private final int durability;


    public BetterTool(Properties props) {
        super(props);
        //this.tier = BetterToolType.BETTER_PICKAXE_TYPE.getToolTier(); Think this might be good later? Make new tier types based on input materials? for now this is useless tho
        this.blocks = BlockTags.MINEABLE_WITH_PICKAXE;
        this.durability = 60; //Should be based on material.
        this.speed = 9.0f; //Should be based on material.
        this.attackDamageBase = 2.0f; //Should be based on material.
    }

    public static String getName() {
        return name;
    }

    //Basic properties

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false; //For now, maybe have like... a modifier that changes this? would be cool :D
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false; //Same as above
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false; //Same as above
    }

    //Display

    @Override //Will it enchanted glow basically
    public boolean isFoil(@NotNull ItemStack itemStack) {
        return false; //For now, maybe let a modifier do it later
    }

    //Damage / Durability
    //Anyone else think its dumb that its called damage ?? just stick to durability??? or am i missing something
    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        return false; //Gonna have a custom Tinkers 1.12 style system for this
    }

    @Override
    public boolean canBeDepleted() {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getDamage(stack) == durability ? durability + 1 : durability;
    }

    @Override
    public int getDamage(ItemStack stack) {
        return !stack.hasTag() ? 0 : stack.getTag().getInt("Damage");
    }

    //Atacking Logic (hooh boy this is where it gets cringe)

    @Override
    public boolean onLeftClickEntity(ItemStack itemStack, Player player, Entity entity) {
        //Durability handling
        if(getDamage(itemStack) == durability) {
            return super.onLeftClickEntity(itemStack, player, entity); // Return early if its broken
        }

        //Doing damage handling
        float attackDamage = attackDamageBase; //This should change based on various factors, like are we critting? etc.
        entity.hurt(DamageSource.playerAttack(player), attackDamage);

        //Damaging the durability handling
        int damageAmount = 1;
        itemStack.hurtAndBreak(damageAmount, player, (p_40992_) -> {
            p_40992_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });

        //EXP handling
        int exp_per_kill = 1; //Arbitrary
        if(!entity.isAlive()) { //If the entity is no longer alive after being hurt, meaning we killed it
            addExp(itemStack, exp_per_kill, player); //Add exp
        }

        return super.onLeftClickEntity(itemStack, player, entity);
    }

    //Levelling up (putting this here cus im lazy)
    void levelUp(ItemStack itemStack, Player player) {
        CompoundTag nbt = itemStack.getTag();
        CompoundTag levelNbt = (CompoundTag)nbt.get(LEVEL_DATA_REF);
        CompoundTag modifierNbt = (CompoundTag)nbt.get(MODIFIERS_DATA_REF);

        levelNbt.putInt(LEVEL_REF, levelNbt.getInt(LEVEL_REF) + 1);
        levelNbt.putInt(EXP_REF, levelNbt.getInt(EXP_REF) - levelNbt.getInt(LEVEL_UP_EXP_REF));
        levelNbt.putInt(LEVEL_UP_EXP_REF, levelNbt.getInt(LEVEL_UP_EXP_REF) + 1);
        modifierNbt.putInt(AVAILABLE_MODIFIERS_REF, modifierNbt.getInt(AVAILABLE_MODIFIERS_REF) + 1);

        player.sendSystemMessage(Component.literal("You levelled up to level " + levelNbt.getInt(LEVEL_REF) + "!"));
    }

    //Adding exp
    void addExp(ItemStack itemStack, int amountToAdd, Player player) {
        CompoundTag nbt = itemStack.getTag();
        CompoundTag levelNbt = (CompoundTag)nbt.get(LEVEL_DATA_REF);

        levelNbt.putInt(EXP_REF, levelNbt.getInt(EXP_REF) + amountToAdd);

        if(levelNbt.getInt(EXP_REF) >= levelNbt.getInt(LEVEL_UP_EXP_REF)) {
            //Level up!!!
            levelUp(itemStack, player);
        }
    }

    //Harvesting Logic (:3)

    @Override //Maybe good? think its for like when u cant mine dia's with wood. dunno
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(blocks) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(Tiers.DIAMOND, state); //This should be changeable lol
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack itemStack, Level level, @NotNull BlockState blockState, @NotNull BlockPos blockPos, @NotNull LivingEntity player) {
        if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            itemStack.hurtAndBreak(1, player, (p_40992_) -> {
                p_40992_.broadcastBreakEvent(EquipmentSlot.MAINHAND);

            });
        }

        //adds EXP
        int exp_per_block = 1; //Arbitrary
        addExp(itemStack, exp_per_block, (Player)player);

        return true;
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack itemStack, @NotNull BlockState blockState) {
        if(getDamage(itemStack) == durability) {
            return 0.0f;
        }

        if (blockState.is(this.blocks)) {

            if(itemStack.getTag() != null && itemStack.getTag().contains("speed")) {
                return this.speed * itemStack.getTag().getInt("speed"); //This sucks. Needs to be based on individual itemstacks, not the tool class.
                //Remake itemStack into something like a toolStack? where like it has these extra built in things that I need
            }else {
                return this.speed; // Same here
            }
        }
        return 1.0F; //This is just for everything that isn't pickaxe material lol
    }

    //Inventory Tick Stuff
    @Override
    public void inventoryTick(ItemStack itemStack, @NotNull Level level, @NotNull Entity player, int slot, boolean selected) {
        CompoundTag originalNbt = itemStack.getTag();

        //This runs twice. No clue why. Wish I knew why. TODO: FIX THIS!
        if(originalNbt != null && !originalNbt.contains(LEVEL_DATA_REF)) {
            System.out.println("INITIALIZING THE FREAKING THINGY!!!!!!!!!!!!!!!");
            //Put default nbt
            CompoundTag nbt = new CompoundTag();
            itemStack.setTag(nbt);

            //Level nbt
            CompoundTag levelNbt = new CompoundTag();
            levelNbt.putInt(EXP_REF, 0);
            levelNbt.putInt(LEVEL_UP_EXP_REF, 1);
            levelNbt.putInt(LEVEL_REF, 1);
            nbt.put(LEVEL_DATA_REF, levelNbt);

            //Modifier nbt
            CompoundTag modifierNbt = new CompoundTag();
            modifierNbt.putInt(AVAILABLE_MODIFIERS_REF, 0);
            modifierNbt.put(MODIFIERS_REF, new CompoundTag());
            nbt.put(MODIFIERS_DATA_REF, modifierNbt);
        }
    }

    //Tooltip

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        List<Component> componentsToAdd = makeToolTip(itemStack);
        components.addAll(componentsToAdd);
        components.add(Component.literal("The rest of nbt: " + itemStack.getTag())); //TODO remove this later
    }

    public List<Component> makeToolTip(ItemStack itemStack) {
        //Get item data
        CompoundTag nbt = itemStack.getTag();
        if(nbt == null) { return new ArrayList<>(); }
        CompoundTag levelNbt = (CompoundTag) nbt.get(LEVEL_DATA_REF);
        if(levelNbt == null) { return new ArrayList<>(); }
        CompoundTag modifierNbt = (CompoundTag) nbt.get(MODIFIERS_DATA_REF);
        if(modifierNbt == null) { return new ArrayList<>(); }

        //Make the tooltip
        List<Component> components = new ArrayList<>();

        //Durability tooltip
        Component final_durr_msg_tt;
        if(getDamage(itemStack) != durability) {
            Component durr_tt = Component.literal(String.valueOf(durability - getDamage(itemStack))).withStyle(ChatFormatting.GREEN);
            Component total_durr_tt = Component.literal(String.valueOf(durability)).withStyle(ChatFormatting.GREEN);
            final_durr_msg_tt = Component.empty().append(durr_tt).append("/").append(total_durr_tt);
        }else {
            final_durr_msg_tt = Component.literal("Broken").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD);
        }
        components.add(Component.literal("Durability: ").append(final_durr_msg_tt));

        //Level tooltip
        Component level_tt = Component.literal(String.valueOf(levelNbt.getInt(LEVEL_REF))).withStyle(ChatFormatting.YELLOW);
        Component final_level_msg_tt = Component.literal("Level: ").append(level_tt);
        components.add(final_level_msg_tt);

        //Exp tooltip
        Component exp_tt = Component.literal(String.valueOf(levelNbt.getInt(EXP_REF))).withStyle(ChatFormatting.YELLOW);
        Component level_up_exp_tt = Component.literal(String.valueOf(levelNbt.getInt(LEVEL_UP_EXP_REF))).withStyle(ChatFormatting.YELLOW);
        Component final_exp_tt = Component.literal("Exp: ").append(exp_tt).append("/").append(level_up_exp_tt);
        components.add(final_exp_tt);

        //Available modifiers tooltip
        Component avail_modif_tt = Component.literal(String.valueOf(modifierNbt.getInt(AVAILABLE_MODIFIERS_REF))).withStyle(ChatFormatting.WHITE);
        Component final_modif_tt = Component.literal("Modifiers: ").append(avail_modif_tt);
        components.add(final_modif_tt);

        //Actual modifiers tooltip
        CompoundTag modifiers = (CompoundTag)modifierNbt.get(MODIFIERS_REF);

        for(var key: modifiers.getAllKeys()) {
            //System.out.println(key);
            Modifier curr_mod = Modifier.keyToModifier(key);
            int curr_level = modifiers.getInt(key);

            components.add(Modifier.getCustomDisplay(curr_mod, curr_level));
        }
        //System.out.println();
        //Lets do this later

        return components;
    }
}
