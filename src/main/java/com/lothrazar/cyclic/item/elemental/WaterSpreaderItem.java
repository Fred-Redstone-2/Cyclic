/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.cyclic.item.elemental;

import com.lothrazar.cyclic.item.ItemBaseCyclic;
import com.lothrazar.library.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class WaterSpreaderItem extends ItemBaseCyclic {

  private static final int COOLDOWN = 28;
  public static ModConfigSpec.IntValue RADIUS;

  public WaterSpreaderItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    BlockPos pos = context.getClickedPos();
    Direction side = context.getClickedFace();
    boolean isLevelClientSide = context.getLevel().isClientSide();

    if (spreadWaterFromCenter(context.getLevel(), pos.relative(side))) {
      if (player != null) {
        EntityUtil.setCooldownItem(player, this, COOLDOWN);
        SoundUtil.playSound(player, SoundEvents.PLAYER_SPLASH);
        player.swing(context.getHand());

        if (!isLevelClientSide) {
          ItemStackUtil.damageItem(player, context.getItemInHand());
        }
      }

      return InteractionResult.sidedSuccess(isLevelClientSide);
    }
    return super.useOn(context);
  }

  private boolean spreadWaterFromCenter(Level world, BlockPos posCenter) {
    int count = 0;
    final BlockState waterState = Blocks.WATER.defaultBlockState();
    List<BlockPos> blocks = ShapeUtil.squareHorizontalFull(posCenter, RADIUS.get());

    for (BlockPos pos : blocks) {
      FluidState fluid = world.getFluidState(pos);
      if (fluid.is(Fluids.FLOWING_WATER) && !fluid.isSource()) {
        world.setBlockAndUpdate(pos, waterState);
        count++;
      } else {
        BlockState state = world.getBlockState(pos);
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)
                && !state.getValue(BlockStateProperties.WATERLOGGED)
                && this.isWaterNextdoor(world, pos)) {
          // Flow it into the loggable
          state = state.setValue(BlockStateProperties.WATERLOGGED, true);
          world.setBlockAndUpdate(pos, state);
          count++;
        }
      }
    }
    return count > 0;
  }

  private boolean isWaterNextdoor(Level world, BlockPos pos) {
    return world.isWaterAt(pos.north()) || world.isWaterAt(pos.south()) ||
        world.isWaterAt(pos.east()) || world.isWaterAt(pos.west());
  }
}
