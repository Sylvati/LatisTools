package lati.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BetterTool extends Item {
    private static final String name = "better_tool";
    private final Tier tier;

    private final TagKey<Block> blocks;
    private final float speed;
    private float attackDamageBaseline;
    private Multimap<Attribute, AttributeModifier> defaultModifiers;


    public BetterTool(Properties props) {
        super(props);
        this.tier = BetterToolType.BETTER_PICKAXE_TYPE.getToolTier();

        this.blocks = BlockTags.MINEABLE_WITH_PICKAXE;
        this.speed = tier.getSpeed();
        this.attackDamageBaseline = (float)1;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamageBaseline, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)1, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    //Unique?

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        CompoundTag nbtTagCompound = itemStack.getTag();
        //System.out.println(nbtTagCompound);
        if(nbtTagCompound == null) { return; }
        //components.add(Component.literal("Skibidi toilet rizzing up baby gronk.").withStyle(ChatFormatting.WHITE));
        components.add(Component.literal("Level: " + nbtTagCompound.getInt("level")));
        components.add(Component.literal("Exp: " + nbtTagCompound.getInt("exp") + "/" + nbtTagCompound.getInt("level_up_exp")).withStyle(ChatFormatting.WHITE));
        components.add(Component.literal("Modifiers: " + nbtTagCompound.getInt("available_modifiers")).withStyle(ChatFormatting.WHITE));
        if(nbtTagCompound.contains("sharpness")) { components.add(Component.literal("Sharpness: " + nbtTagCompound.getInt("sharpness")).withStyle(ChatFormatting.BOLD)); }
        if(nbtTagCompound.contains("speed")) { components.add(Component.literal("Speed: " + nbtTagCompound.getInt("speed")).withStyle(ChatFormatting.RED)); }

        components.add(Component.literal("The rest of nbt: " + nbtTagCompound));
    }

    //End Unique

    //SwordItem-based Overrides or Definitions
    public float getDamage() {
        return this.attackDamageBaseline;
    }

    @Override
    public boolean canAttackBlock(BlockState p_43291_, Level p_43292_, BlockPos p_43293_, Player player) { //Idk what the rest of those are so lol, just leaving them
        return !player.isCreative();
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity enemy, LivingEntity player) { //These variable names are afaik, not certain of player and enemy
        itemStack.hurtAndBreak(1, player, (p_43296_) -> p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    //End SwordItem-based

    //DiggerItem-based
    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        if (blockState.is(this.blocks)) {
            if(itemStack.getTag() != null && itemStack.getTag().contains("speed")) {
                return this.tier.getSpeed() * itemStack.getTag().getInt("speed");
            }else {
                return this.tier.getSpeed();
            }
        }
        return 1.0F;
    }

    public float getAttackDamage() { //Not an override, but here for some reason in DiggerItem? Including just to be safe ig
        return this.attackDamageBaseline;
    }

    //End DiggerItem-based

    //Both-based

    @Override //Maybe good? think its for like when u cant mine dia's with wood. dunno
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(blocks) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(this.tier, state);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) { //Can sword or pick
        return (net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction) || ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    //End both-based

    //Neither / misc / im too lazy based


    @Override
    public void inventoryTick(ItemStack itemStack, @NotNull Level level, Entity player, int slot, boolean selected) {
        CompoundTag nbtTagCompound = itemStack.getTag();
        if(nbtTagCompound == null) {
            nbtTagCompound = new CompoundTag();
            itemStack.setTag(nbtTagCompound);
            nbtTagCompound.putInt("exp", 0);
            nbtTagCompound.putInt("level_up_exp", 1);
            nbtTagCompound.putInt("level", 1);
            nbtTagCompound.putInt("available_modifiers", 0);
        }
        if(nbtTagCompound.contains("sharpness")) {
            /* This does technically work, but modifies EVERY instance of the item's stats. Very weird
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamageBaseline + 3 * nbtTagCompound.getInt("sharpness"), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)1, AttributeModifier.Operation.ADDITION));
            this.defaultModifiers = builder.build();
             */
        }

    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity player) {
        if (!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            itemStack.hurtAndBreak(1, player, (p_40992_) -> {
                p_40992_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        CompoundTag nbtTagCompound = itemStack.getTag();
        if(nbtTagCompound == null) { return false; }

        nbtTagCompound.putInt("exp",  nbtTagCompound.getInt("exp") + 1); //Every block broken will give 1 exp

        if(nbtTagCompound.getInt("exp") >= nbtTagCompound.getInt("level_up_exp")) {
            //Level up!!!
            nbtTagCompound.putInt("level", nbtTagCompound.getInt("level") + 1);
            nbtTagCompound.putInt("exp", nbtTagCompound.getInt("exp") - nbtTagCompound.getInt("level_up_exp"));
            nbtTagCompound.putInt("level_up_exp", nbtTagCompound.getInt("level_up_exp") + 1);
            nbtTagCompound.putInt("available_modifiers", nbtTagCompound.getInt("available_modifiers") + 1);

            player.sendSystemMessage(Component.literal("You levelled up to level " + nbtTagCompound.getInt("level") + "!"));
        }

        return true;
    }

    public static String getName() {
        return name;
    }
}
