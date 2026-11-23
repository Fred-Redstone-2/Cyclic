package com.lothrazar.cyclic.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class CapabilityUtil {

  public static boolean isItem(Direction facing, Level level, BlockPos facingPos) {
    if (facing == null) {
      return false;
    }
    return level.getCapability(Capabilities.Item.BLOCK, facingPos, facing.getOpposite()) != null;
  }

  public static boolean isFluid(Direction facing, Level level, BlockPos facingPos) {
    if (facing == null) {
      return false;
    }
    return level.getCapability(Capabilities.Fluid.BLOCK, facingPos, facing.getOpposite()) != null;
  }

  public static boolean isEnergy(Direction facing, Level level, BlockPos facingPos) {
    if (facing == null) {
      return false;
    }
    return level.getCapability(Capabilities.Energy.BLOCK, facingPos, facing.getOpposite()) != null;
  }

  public static IEnergyStorage energy(ItemStack stack) {
    if (stack.isEmpty()) {
      return null;
    }
    var handler = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
    return handler == null ? null : IEnergyStorage.of(handler);
  }

  public static IEnergyStorage energy(Level level, BlockPos pos) {
    return energy(level, pos, null);
  }

  public static int energyStored(Level level, BlockPos pos) {
    var handler = level.getCapability(Capabilities.Energy.BLOCK, pos, null);
    return handler == null ? 0 : handler.getAmountAsInt();
  }

  public static IEnergyStorage energy(Level level, BlockPos pos, Direction dir) {
    var handler = level.getCapability(Capabilities.Energy.BLOCK, pos, dir);
    return handler == null ? null : IEnergyStorage.of(handler);
  }

  public static IFluidHandler fluid(Level level, BlockPos pos) {
    return fluid(level, pos, (Direction) null);
  }

  public static IFluidHandler fluid(Level level, BlockPos pos, BlockHitResult dir) {
    return fluid(level, pos, dir.getDirection());
  }

  public static IFluidHandler fluid(ItemStack stack) {
    if (stack.isEmpty()) {
      return null;
    }
    var handler = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
    return handler == null ? null : IFluidHandler.of(handler);
  }

  public static IFluidHandler fluid(Level level, BlockPos pos, Direction dir) {
    var handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, dir);
    return handler == null ? null : IFluidHandler.of(handler);
  }

  public static ResourceHandler<FluidResource> fluidResources(Level level, BlockPos pos, Direction dir) {
    return level.getCapability(Capabilities.Fluid.BLOCK, pos, dir);
  }

  public static IItemHandler item(Level level, BlockPos pos) {
    return item(level, pos, null);
  }

  public static IItemHandler item(Level level, BlockPos pos, Direction dir) {
    var handler = level.getCapability(Capabilities.Item.BLOCK, pos, dir);
    return handler == null ? null : IItemHandler.of(handler);
  }

  public static IItemHandler item(ItemStack stack) {
    if (stack.isEmpty()) {
      return null;
    }
    var handler = stack.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(stack));
    return handler == null ? null : IItemHandler.of(handler);
  }
}
