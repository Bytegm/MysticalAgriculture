package com.blakebr0.mysticalagriculture.client.screen;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.mysticalagriculture.MysticalAgriculture;
import com.blakebr0.mysticalagriculture.container.SoulExtractorContainer;
import com.blakebr0.mysticalagriculture.tileentity.SoulExtractorTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SoulExtractorScreen extends BaseContainerScreen<SoulExtractorContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MysticalAgriculture.MOD_ID, "textures/gui/soul_extractor.png");
    private SoulExtractorTileEntity tile;

    public SoulExtractorScreen(SoulExtractorContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 176, 194);
    }

    @Override
    protected void init() {
        super.init();

        this.tile = this.getTileEntity();
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        var title = this.getTitle().getString();

        this.font.draw(stack, title, (float) (this.imageWidth / 2 - this.font.width(title) / 2), 6.0F, 4210752);
        this.font.draw(stack, this.playerInventoryTitle, 8.0F, (float) (this.imageHeight - 96 + 2), 4210752);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        this.renderDefaultBg(stack, partialTicks, mouseX, mouseY);

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        int i1 = this.getEnergyBarScaled(78);

        this.blit(stack, x + 7, y + 95 - i1, 176, 109 - i1, 15, i1);

        if (this.getFuelItemValue() > 0) {
            int lol = this.getBurnLeftScaled(13);
            this.blit(stack, x + 31, y + 52 - lol, 176, 12 - lol, 14, lol + 1);
        }

        if (this.getProgress() > 0) {
            int i2 = this.getProgressScaled(24);
            this.blit(stack, x + 98, y + 51, 176, 14, i2 + 1, 16);
        }
    }

    @Override
    protected void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        super.renderTooltip(stack, mouseX, mouseY);

        if (mouseX > x + 7 && mouseX < x + 20 && mouseY > y + 17 && mouseY < y + 94) {
            var text = Component.literal(number(this.getEnergyStored()) + " / " + number(this.getMaxEnergyStored()) + " FE");
            this.renderTooltip(stack, text, mouseX, mouseY);
        }

        if (mouseX > x + 30 && mouseX < x + 45 && mouseY > y + 39 && mouseY < y + 53) {
            var text = Component.literal(number(this.getFuelLeft()) + " FE");
            this.renderTooltip(stack, text, mouseX, mouseY);
        }
    }

    private SoulExtractorTileEntity getTileEntity() {
        var level = this.getMinecraft().level;

        if (level != null) {
            var tile = level.getBlockEntity(this.getMenu().getPos());

            if (tile instanceof SoulExtractorTileEntity extractor) {
                return extractor;
            }
        }

        return null;
    }

    public int getProgress() {
        if (this.tile == null)
            return 0;

        return this.tile.getProgress();
    }

    public int getOperationTime() {
        if (this.tile == null)
            return 0;

        var tier = this.tile.getMachineTier();
        if (tier == null)
            return this.tile.getOperationTime();

        return (int) (this.tile.getOperationTime() * tier.getOperationTimeMultiplier());
    }

    public int getFuelLeft() {
        if (this.tile == null)
            return 0;

        return this.tile.getFuelLeft();
    }

    public int getFuelItemValue() {
        if (this.tile == null)
            return 0;

        return this.tile.getFuelItemValue();
    }

    public int getEnergyStored() {
        if (this.tile == null)
            return 0;

        return this.tile.getEnergy().getEnergyStored();
    }

    public int getMaxEnergyStored() {
        if (this.tile == null)
            return 0;

        var tier = this.tile.getMachineTier();
        if (tier != null) {
            return (int) (this.tile.getEnergy().getMaxEnergyStored() * tier.getFuelCapacityMultiplier());
        }

        return this.tile.getEnergy().getMaxEnergyStored();
    }

    public int getProgressScaled(int pixels) {
        int i = this.getProgress();
        int j = this.getOperationTime();
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    public int getEnergyBarScaled(int pixels) {
        int i = Math.min(this.getEnergyStored(), this.getMaxEnergyStored());
        int j = this.getMaxEnergyStored();
        return (int) (j != 0 && i != 0 ? (long) i * pixels / j : 0);
    }

    public int getBurnLeftScaled(int pixels) {
        int i = this.getFuelLeft();
        int j = this.getFuelItemValue();
        return (int) (j != 0 && i != 0 ? (long) i * pixels / j : 0);
    }
}
