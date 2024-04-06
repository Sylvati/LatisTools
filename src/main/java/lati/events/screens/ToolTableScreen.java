package lati.events.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lati.LatisTools;
import lati.menus.ToolTableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ToolTableScreen extends AbstractContainerScreen<ToolTableMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(LatisTools.MODID, "textures/gui/tool_table.png");

    public ToolTableScreen(ToolTableMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }



    /* THIS IS HOW U DO THAT
    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        Slot slotZero = this.getMenu().getSlot(0);
        if(slotZero.hasItem()) {
            ItemStack slotZeroItem = slotZero.getItem();
            CompoundTag slotZeroNbt = slotZeroItem.getTag();
            this.minecraft.font.draw(poseStack, slotZeroNbt.toString(), this.getXSize() / 2 - this.minecraft.font.width(slotZeroNbt.toString()), 0, 16777215);
        }
        super.renderLabels(poseStack, mouseX, mouseY);
    }*/
}
