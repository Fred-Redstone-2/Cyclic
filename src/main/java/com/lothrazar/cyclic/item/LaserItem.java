package com.lothrazar.cyclic.item;

import com.lothrazar.library.util.TagDataUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class LaserItem extends ItemHasEnergy {

  private static final String NBT_TAG = "damage_cooldown";

  public LaserItem(Properties properties) {
    super(properties.stacksTo(1));
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    return UseAnim.NONE;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
    ItemStack itemstack = playerIn.getItemInHand(handIn);
    playerIn.startUsingItem(handIn);
    return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
  }

  @Override
  public int getUseDuration(ItemStack stack, LivingEntity entity) {
    return 72000 * 2;
  }

  public static ItemStack getIfHeld(Player player) {
    ItemStack heldItem = player.getMainHandItem();
    if (heldItem.getItem() instanceof LaserItem) {
      return heldItem;
    }
    //MAIN HAND ONLY for this case 
    return ItemStack.EMPTY;
  }

  @Override
  public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int chargeTimer) {}

  public static void resetStackDamageCool(ItemStack stack, long gametime) {
    TagDataUtil.setItemStackNBTVal(stack, NBT_TAG, gametime);
  }

  public static long getDamageCooldown(ItemStack stack) {
    long thisOne = TagDataUtil.getItemStackNBT(stack).getLong(NBT_TAG);
    return thisOne;
  }
}
