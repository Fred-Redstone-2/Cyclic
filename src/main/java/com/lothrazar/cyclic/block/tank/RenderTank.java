package com.lothrazar.cyclic.block.tank;

import com.lothrazar.cyclic.util.CapabilityUtil;
import com.lothrazar.cyclic.util.FluidHelpers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public class RenderTank implements BlockEntityRenderer<TileTank, RenderTank.State> {

  public static class State extends BlockEntityRenderState {
    TileTank blockEntity;
  }

  public RenderTank(BlockEntityRendererProvider.Context d) {
  }

  @Override
  public State createRenderState() {
    return new State();
  }

  @Override
  public void extractRenderState(TileTank blockEntity, State state, float partialTicks, Vec3 cameraPosition,
                                 ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    state.blockEntity = blockEntity;
  }

  @Override
  public void submit(State state, PoseStack matrix, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
    TileTank tankHere = state.blockEntity;
    int light = state.lightCoords;
    ResourceHandler<FluidResource> handler = CapabilityUtil.fluidResources(tankHere.getLevel(), tankHere.getBlockPos(), null);
    if (handler == null || handler.size() == 0) {
      return;
    }
    FluidStack fluid = handler.getResource(0).toStack(handler.getAmountAsInt(0));
    if (fluid.isEmpty()) {
      return;
    }
    matrix.pushPose();
    matrix.scale(1F, FluidHelpers.getScale(tankHere.tank), 1F);
    FluidHelpers.submitFluidModel(submitNodeCollector, matrix, fluid, FluidHelpers.STAGES - 1, light);
    matrix.popPose();
  }
}
