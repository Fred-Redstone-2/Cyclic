package com.lothrazar.cyclic.block.sprinkler;

import com.lothrazar.cyclic.render.SpinModelRenderer;
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

public class RenderSprinkler implements BlockEntityRenderer<TileSprinkler, RenderSprinkler.State> {

  public static class State extends BlockEntityRenderState {
    TileSprinkler blockEntity;
    float partialTicks;
  }

  public RenderSprinkler(BlockEntityRendererProvider.Context d) {
  }

  @Override
  public State createRenderState() {
    return new State();
  }

  @Override
  public void extractRenderState(TileSprinkler blockEntity, State state, float partialTicks, Vec3 cameraPosition,
                                 ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    state.blockEntity = blockEntity;
    state.partialTicks = partialTicks;
  }

  @Override
  public void submit(State state, PoseStack matrix, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
    TileSprinkler tankHere = state.blockEntity;
    float v = state.partialTicks;
    int light = state.lightCoords;
    ResourceHandler<FluidResource> handler = CapabilityUtil.fluidResources(tankHere.getLevel(), tankHere.getBlockPos(), null);
    FluidStack fluid = handler == null || handler.size() == 0 ? FluidStack.EMPTY : handler.getResource(0).toStack(handler.getAmountAsInt(0));
    boolean spinning = fluid != null && !fluid.isEmpty();
    float angle = 0F;
    if (spinning && tankHere.getLevel() != null) {
      angle = ((tankHere.getLevel().getGameTime() % 60L) + v) * 6F;
    }
    SpinModelRenderer.render(SpinModelRenderer.SPRINKLER_SPIN, angle, matrix, submitNodeCollector, light, 0);
    if (!spinning) {
      return;
    }
    matrix.pushPose();
    matrix.scale(1F, FluidHelpers.getScale(tankHere.tank) / 4F, 1F);
    float f = 0.5F;
    matrix.scale(f, 1F, f);
    matrix.translate(f, 0, f);
    FluidHelpers.submitFluidModel(submitNodeCollector, matrix, fluid, FluidHelpers.STAGES - 1, light);
    matrix.popPose();
  }
}
