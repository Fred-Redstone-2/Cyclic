package com.lothrazar.cyclic.block.melter;

import com.lothrazar.cyclic.util.CapabilityUtil;
import com.lothrazar.cyclic.util.FluidHelpers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.Nullable;

public class RenderMelter implements BlockEntityRenderer<TileMelter, RenderMelter.State> {

  public static class State extends BlockEntityRenderState {
    TileMelter blockEntity;
    ItemStackRenderState item;
  }

  private final ItemModelResolver itemModelResolver;

  public RenderMelter(BlockEntityRendererProvider.Context d) {
    this.itemModelResolver = d.itemModelResolver();
  }

  @Override
  public State createRenderState() {
    return new State();
  }

  @Override
  public void extractRenderState(TileMelter tankHere, State state, float partialTicks, Vec3 cameraPosition,
                                 ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
    BlockEntityRenderer.super.extractRenderState(tankHere, state, partialTicks, cameraPosition, breakProgress);
    state.blockEntity = tankHere;
    IItemHandler itemHandler = CapabilityUtil.item(tankHere.getLevel(), tankHere.getBlockPos());
    if (itemHandler != null) {
      ItemStack stack = itemHandler.getStackInSlot(0);
      if (!stack.isEmpty()) {
        ItemStackRenderState itemState = new ItemStackRenderState();
        itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.GROUND, tankHere.getLevel(), null, 0x111111);
        state.item = itemState;
      }
    }
  }

  @Override
  public void submit(State state, PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
    TileMelter tankHere = state.blockEntity;
    int light = state.lightCoords;
    if (state.item != null) {
      matrixStack.pushPose();
      matrixStack.translate(0.5, 0.60, 0.5);
      state.item.submit(matrixStack, submitNodeCollector, light, OverlayTexture.NO_OVERLAY, 0);
      matrixStack.popPose();
    }
    ResourceHandler<FluidResource> handler = CapabilityUtil.fluidResources(tankHere.getLevel(), tankHere.getBlockPos(), null);
    if (handler == null || handler.size() == 0) {
      return;
    }
    FluidStack fluid = handler.getResource(0).toStack(handler.getAmountAsInt(0));
    if (fluid.isEmpty()) {
      return;
    }
    matrixStack.pushPose();
    matrixStack.scale(1F, FluidHelpers.getScale(tankHere.tank), 1F);
    FluidHelpers.submitFluidModel(submitNodeCollector, matrixStack, fluid, FluidHelpers.STAGES - 1, light);
    matrixStack.popPose();
  }
}
