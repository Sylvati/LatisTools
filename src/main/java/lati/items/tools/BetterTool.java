package lati.items.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lati.items.tools.helper.ToolAttackUtil;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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

    public static String getName() {
        return name;
    }

    //Basic properties

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
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
    public boolean isFoil(ItemStack itemStack) {
        return false; //For now, maybe let a modifier do it later
    }

    //Damage / Durability
    //Anyone else think its dumb that its called damage ?? just stick to durability??? or am i missing something
    @Override
    public boolean isRepairable(ItemStack stack) {
        return false; //Gonna have a custom Tinkers 1.12 style system for this
    }

    @Override
    public boolean canBeDepleted() {
        return false; //Just no for now
    }

    //Atacking Logic (hooh boy this is where it gets cringe)

    /*TODO: incomplete lol
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return ToolAttackUtil.attackEntity(stack, player, entity);
    }*/


    //Harvesting Logic (:3)

    @Override //Maybe good? think its for like when u cant mine dia's with wood. dunno
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.is(blocks) && net.minecraftforge.common.TierSortingRegistry.isCorrectTierForDrops(this.tier, state);
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

    //Inventory Tick Stuff

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

    //Tooltip

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


}
