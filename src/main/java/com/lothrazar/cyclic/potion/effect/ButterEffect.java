package com.lothrazar.cyclic.potion.effect;

import java.util.Arrays;
import java.util.List;
import com.lothrazar.cyclic.potion.CyclicMobEffect;
import com.lothrazar.library.core.Const;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ButterEffect extends CyclicMobEffect {

  private static final double DROP_CHANCE = 0.06;

  public ButterEffect(MobEffectCategory typeIn, int liquidColorIn) {
    super(typeIn, liquidColorIn);
  }

  @Override
  public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
    return true;
  }

  @Override
  public boolean applyEffectTick(LivingEntity entity, int amplifier) {
    // Only execute on server
    if (!entity.level().isClientSide() && entity instanceof Player player) {
      // Sprinting or jumping
      if (!player.onGround() || player.isSprinting()) {
        if (player.getRandom().nextDouble() < DROP_CHANCE) {
          this.dropRandomItem(player, amplifier);
        }
      }
    }
    return true;
  }

  private void dropRandomItem(Player player, int amplifier) {
    List<EquipmentSlot> slots;
    if (amplifier == Const.Potions.I) {
      slots = Arrays.asList(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    } else {
      slots = Arrays.asList(EquipmentSlot.values());
    }

    int randomSlot = player.getRandom().nextInt(slots.size());
    EquipmentSlot slot = slots.get(randomSlot);
    ItemStack stack = player.getItemBySlot(slot);

    if (!stack.isEmpty()) {
      final boolean dropRandom = true, retainOwnership = false;
      player.drop(stack, dropRandom, retainOwnership);
      player.setItemSlot(slot, ItemStack.EMPTY);
    }
  }
}
