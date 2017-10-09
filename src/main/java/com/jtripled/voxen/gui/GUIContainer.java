package com.jtripled.voxen.gui;

import com.jtripled.voxen.inventory.ContainerBase;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author jtripled
 */
public abstract class GUIContainer extends GuiContainer
{
    private final ContainerBase container;
    
    public GUIContainer(ContainerBase container)
    {
        super(container);
        this.container = container;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
    {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        fontRenderer.drawString("Inventory", 8, ySize - 93, 0x404040);
        fontRenderer.drawString(getName(), 8, 6, 0x404040);
        drawBackground(ticks, mouseX, mouseY, x, y);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawDefaultBackground();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(getTexture());
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        drawForeground(mouseX, mouseY, x, y);
    }
    
    public void drawBackground(float ticks, int mouseX, int mouseY, int x, int y)
    {
        
    }
    
    public void drawForeground(int mouseX, int mouseY, int x, int y)
    {
        
    }
    
    public abstract ResourceLocation getTexture();
    
    public abstract String getName();
}
