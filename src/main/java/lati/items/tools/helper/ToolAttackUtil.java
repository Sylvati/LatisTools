package lati.items.tools.helper;

import net.minecraft.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;

import java.util.function.DoubleSupplier;

public class ToolAttackUtil {
    public static DoubleSupplier getCooldownFunction(Player player, InteractionHand hand) {
        //if(hand == InteractionHand.OFF_HAND) {} Do something, maybe ? eventually
        return () -> player.getAttackStrengthScale(0.5f);
    }

    public static EquipmentSlot getSlotType(InteractionHand hand) {
        return hand == InteractionHand.OFF_HAND ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
    }

    public static LivingEntity getLivingEntity(Entity entity) {
        if(entity instanceof PartEntity<?> part) {
            entity = part.getParent();
        }
        return entity instanceof LivingEntity living ? living : null;
    }

    public static float getAttributeAttackDamage(ItemStack tool, LivingEntity holder, EquipmentSlot slotType) {
        if(slotType == EquipmentSlot.MAINHAND || holder.level.isClientSide) {
            return (float) holder.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }
        //We dont have modifiers implemented yet, so just do both either way
        return (float) holder.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    /*
    Base attack logic. Hell
     */

    //General attackEntity. Cooldown, no projectile
    public static boolean attackEntity(ItemStack tool, Player attacker, Entity targetEntity) {
        return attackEntity(tool, attacker, InteractionHand.MAIN_HAND, targetEntity, getCooldownFunction(attacker, InteractionHand.MAIN_HAND), false);
    }

    public static boolean attackEntity(ItemStack tool, LivingEntity attackerLiving, InteractionHand hand, Entity targetEntity, DoubleSupplier cooldownFunction, boolean isExtraAttack) {
        return attackEntity(tool, attackerLiving, hand, targetEntity, cooldownFunction, isExtraAttack, getSlotType(hand));
    }

    public static boolean attackEntity(ItemStack tool, LivingEntity attackerLiving, InteractionHand hand, Entity targetEntity, DoubleSupplier cooldownFunction, boolean isExtraAttack, EquipmentSlot sourceSlot) {
        //Broken? aint my problem no more
        //Cant actually do this logic until I implement a custom toolstack maybe ?

        //nothing to do? cancel the attack
        if(attackerLiving.level.isClientSide || !targetEntity.isAttackable() || targetEntity.skipAttackInteraction(attackerLiving)) {
            return true;
        }

        //Get relevant entities
        LivingEntity targetLiving = getLivingEntity(targetEntity);
        Player attackerPlayer = null;
        if(attackerLiving instanceof Player player) {
            attackerPlayer = player;
        }

        //player base damage
        float damage = getAttributeAttackDamage(tool, attackerLiving, sourceSlot);

        float cooldown = (float)cooldownFunction.getAsDouble();
        boolean fullyCharged = cooldown > 0.9f;

        boolean isCritical = !isExtraAttack && fullyCharged && attackerLiving.fallDistance > 0.0F && !attackerLiving.isOnGround() && !attackerLiving.onClimbable() && !attackerLiving.isInWater() && !attackerLiving.hasEffect(MobEffects.BLINDNESS) && !attackerLiving.isPassenger() && targetLiving != null && !attackerLiving.isSprinting();

        //Incomplete code lol

        return true;
    }
}
