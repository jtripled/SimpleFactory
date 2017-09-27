package com.jtripled.simplefactory.fluid.inventory;

import com.jtripled.simplefactory.fluid.inventory.ContainerFluid;
import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.fluid.TileFluid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author jtripled
 */
public class GUIFluid extends GuiContainer
{
    public static final ResourceLocation TANK_TEXTURE = new ResourceLocation(SimpleFactory.ID, "textures/gui/tank.png");
    public static final ResourceLocation PUMP_TEXTURE = new ResourceLocation(SimpleFactory.ID, "textures/gui/pump.png");
    
    private final TileFluid tile;
    private final Container container;
    private final boolean bucketSlot;
    private final String name;
    
    public GUIFluid(ContainerFluid container)
    {
        super(container);
        this.tile = container.getTile();
        this.container = container;
        this.bucketSlot = tile.hasBucketSlot();
        this.name = SimpleFactory.PROXY.localize(tile.getBlockType().getUnlocalizedName() + ".name");
        this.ySize = 132 + (bucketSlot ? 18 : 0);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString("Inventory", 8, ySize - 93, 0x404040);
        fontRenderer.drawString(name, 8, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(bucketSlot ? PUMP_TEXTURE : TANK_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        int fill = (int) Math.ceil(141 * (float) tile.getInternalTank().getFluidAmount() / tile.getInternalTank().getCapacity());
        drawTexturedModalRect(x + 17, y + 23, 0, 250, fill, 6);
        if (bucketSlot)
        {
            // Draw progress arrow
        }
    }
}
