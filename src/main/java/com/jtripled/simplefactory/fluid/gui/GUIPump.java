package com.jtripled.simplefactory.fluid.gui;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.fluid.container.ContainerPump;
import com.jtripled.voxen.gui.GUIContainerTile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 *
 * @author jtripled
 */
public class GUIPump extends GUIContainerTile<ContainerPump>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(SimpleFactory.ID, "textures/gui/pump.png");
    
    private final FluidTank tank;
    
    public GUIPump(ContainerPump container)
    {
        super(container);
        this.tank = container.getTile().getInternalTank();
        this.ySize = 150;
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, int x, int y)
    {
        if (mouseX > x + 14 && mouseX < x + 161 && mouseY > y + 20 && mouseY < y + 32)
        {
            List<String> tooltip = new ArrayList<>();
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
    public void drawBackground(float ticks, int mouseX, int mouseY, int x, int y)
    {
        FluidStack fluid = tank.getFluid();
        if (fluid != null)
        {
            TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString());
            int fill = (int) Math.ceil(143 * (float) tank.getFluidAmount() / tank.getCapacity());
            int offset = 0;
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            while (fill > 0)
            {
                int xCoord = x + 16 + offset;
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
        int progress = 22 - (int) Math.ceil(22 * (float) this.getContainer().getTile().getBucketCooldown() / 25);
        drawTexturedModalRect(x + 77, y + 37, 176, 0, progress, 16);
        drawTexturedModalRect(x + 17, y + 23, 0, 250, 141, 6);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return TEXTURE;
    }
}
