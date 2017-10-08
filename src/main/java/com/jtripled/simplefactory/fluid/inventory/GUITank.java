package com.jtripled.simplefactory.fluid.inventory;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.fluid.tile.TileFluid;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 *
 * @author jtripled
 */
public class GUITank extends GuiContainer
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(SimpleFactory.ID, "textures/gui/tank.png");
    
    private final TileFluid tile;
    private final Container container;
    private final String name;
    
    public GUITank(ContainerTank container)
    {
        super(container);
        this.tile = container.getTile();
        this.container = container;
        this.name = SimpleFactory.PROXY.localize(tile.getBlockType().getUnlocalizedName() + ".name");
        this.ySize = 132;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString("Inventory", 8, ySize - 93, 0x404040);
        fontRenderer.drawString(name, 8, 6, 0x404040);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        if (mouseX > x + 14 && mouseX < x + 161 && mouseY > y + 20 && mouseY < y + 32)
        {
            List<String> tooltip = new ArrayList<>();
            FluidTank tank = tile.getInternalTank();
            FluidStack fluid = tank.getFluid();
            if (fluid == null)
            {
                TextComponentString message = new TextComponentString("Empty");
                message.getStyle().setColor(TextFormatting.RED);
                tooltip.add(message.getFormattedText());
                message = new TextComponentString("0/" + tank.getCapacity() + "mB");
                message.getStyle().setColor(TextFormatting.GRAY);
                tooltip.add(message.getFormattedText());
            }
            else
            {
                tooltip.add(fluid.getFluid().getLocalizedName(fluid));
                TextComponentString message = new TextComponentString(tank.getFluidAmount() + "/" + tank.getCapacity() + "mB");
                message.getStyle().setColor(TextFormatting.GRAY);
                tooltip.add(message.getFormattedText());
            }
            drawHoveringText(tooltip, mouseX - xSize, mouseY - ySize / 2);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawDefaultBackground();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        FluidTank tank = tile.getInternalTank();
        FluidStack fluid = tank.getFluid();
        if (fluid != null)
        {
            TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString());
            int fill = (int) Math.ceil(141 * (float) tank.getFluidAmount() / tank.getCapacity());
            int offset = 0;
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            while (fill > 0)
            {
                int xCoord = x + 17 + offset;
                int widthIn = fill >= 16 ? 16 : fill;
                int yCoord = y + 23;
                int heightIn = 6;
                bufferbuilder.pos((double)(xCoord + 0), (double)(yCoord + heightIn), (double)this.zLevel).tex((double)texture.getMinU(), (double)texture.getInterpolatedV(6)).endVertex();
                bufferbuilder.pos((double)(xCoord + widthIn), (double)(yCoord + heightIn), (double)this.zLevel).tex(fill >= 16 ? (double)texture.getMaxU() : (double)texture.getInterpolatedU(fill), (double)texture.getInterpolatedV(6)).endVertex();
                bufferbuilder.pos((double)(xCoord + widthIn), (double)(yCoord + 0), (double)this.zLevel).tex(fill >= 16 ? (double)texture.getMaxU() : (double)texture.getInterpolatedU(fill), (double)texture.getMinV()).endVertex();
                bufferbuilder.pos((double)(xCoord + 0), (double)(yCoord + 0), (double)this.zLevel).tex((double)texture.getMinU(), (double)texture.getMinV()).endVertex();
                offset += 16;
                fill -= 16;
            }
            tessellator.draw();
        }
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(x + 17, y + 23, 0, 250, 141, 6);
    }
}
