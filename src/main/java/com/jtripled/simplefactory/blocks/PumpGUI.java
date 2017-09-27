package com.jtripled.simplefactory.blocks;

import com.jtripled.simplefactory.SimpleFactory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author jtripled
 */
public class PumpGUI extends GuiContainer
{
    protected final PumpContainer container;
    protected final String name;
    protected final ResourceLocation texture;

    public PumpGUI(PumpContainer container)
    {
        super(container);
        this.container = container;
        this.name = SimpleFactory.PROXY.localize(container.getTile().getBlockType().getUnlocalizedName() + ".name");
        this.texture = new ResourceLocation(SimpleFactory.ID, "textures/gui/pump.png");
        this.ySize = 150;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString("Inventory", 8, ySize - 93, 0x404040);
        fontRenderer.drawString(name, 8, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
    {
        drawDefaultBackground();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        PumpTile tile = container.getTile();
        //int progress = (int) Math.ceil(22 * tile.getProgressRatio());
        int fill = (int) Math.ceil(141 * (float) tile.tank.getFluidAmount() / tile.tank.getCapacity());
        //drawTexturedModalRect(x + 77, y + 37, xSize, 0, progress, 16);
        drawTexturedModalRect(x + 17, y + 23, 0, 250, fill, 6);
    }
}
