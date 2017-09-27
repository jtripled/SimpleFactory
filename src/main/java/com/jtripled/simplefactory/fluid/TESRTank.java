package com.jtripled.simplefactory.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author jtripled
 */
public class TESRTank extends TileEntitySpecialRenderer<TileTank>
{
    @Override
    public void render(TileTank tile, double x, double y, double z, float ticks, int destroy, float alpha)
    {
        IBlockState state = tile.getWorld().getBlockState(tile.getPos()).getActualState(tile.getWorld(), tile.getPos());
        FluidTank tank = tile.getInternalTank();
        int heightDiff = tile.getPos().getY() - tile.getInternalTankPos().getY();
        int fluidAmount = tank.getFluidAmount() - (heightDiff * 16000);
        int level = fluidAmount >= 16000 ? 16000 : fluidAmount <= 0 ? 0 : fluidAmount;
        if (level <= 0)
            return;
        boolean up = state.getValue(BlockTank.UP);
        boolean down = state.getValue(BlockTank.DOWN);
        boolean renderTop = fluidAmount <= 16000;
        Fluid fluid = tile.getBaseTank().tank.getFluid().getFluid();
        try
        {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (Minecraft.isAmbientOcclusionEnabled()) GL11.glShadeModel(GL11.GL_SMOOTH);
            else GL11.glShadeModel(GL11.GL_FLAT);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());
            double h;
            double f = down ? 0 : 0.001;
            if (up)
                h = ((double) level / 16000d);
            else
                h = ((double) level / 16000d) - 0.001d;
            int color = fluid.getColor();
            int brightness = Minecraft.getMinecraft().world.getCombinedLight(new BlockPos(x, y, z), fluid.getLuminosity());
            int l1 = brightness >> 0x10 & 0xFFFF;
            int l2 = brightness & 0xFFFF;
            int a = color >> 24 & 0xFF;
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            float uMin = texture.getInterpolatedU(0);
            float uMax = texture.getInterpolatedU(16);
            float vMin = texture.getInterpolatedV(0);
            float vMax = texture.getInterpolatedV(16);
            float uMid = texture.getInterpolatedU(level / 1000d);
            
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buff = tess.getBuffer();
            buff.setTranslation(x, y, z);
            buff.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            
            if (renderTop)
            {
                buff.pos(0.001, h, 0.999).color(r, g, b, a).tex(uMin, vMax).lightmap(l1, l2).endVertex();
                buff.pos(0.999, h, 0.999).color(r, g, b, a).tex(uMax, vMax).lightmap(l1, l2).endVertex();
                buff.pos(0.999, h, 0.001).color(r, g, b, a).tex(uMax, vMin).lightmap(l1, l2).endVertex();
                buff.pos(0.001, h, 0.001).color(r, g, b, a).tex(uMin, vMin).lightmap(l1, l2).endVertex();
            }
            
            buff.pos(0.999, f, 0.999).color(r, g, b, a).tex(uMin, vMax).lightmap(l1, l2).endVertex();
            buff.pos(0.999, h, 0.999).color(r, g, b, a).tex(uMid, vMax).lightmap(l1, l2).endVertex();
            buff.pos(0.001, h, 0.999).color(r, g, b, a).tex(uMid, vMin).lightmap(l1, l2).endVertex();
            buff.pos(0.001, f, 0.999).color(r, g, b, a).tex(uMin, vMin).lightmap(l1, l2).endVertex();

            buff.pos(0.001, f, 0.001).color(r, g, b, a).tex(uMin, vMin).lightmap(l1, l2).endVertex();
            buff.pos(0.001, h, 0.001).color(r, g, b, a).tex(uMid, vMin).lightmap(l1, l2).endVertex();
            buff.pos(0.999, h, 0.001).color(r, g, b, a).tex(uMid, vMax).lightmap(l1, l2).endVertex();
            buff.pos(0.999, f, 0.001).color(r, g, b, a).tex(uMin, vMax).lightmap(l1, l2).endVertex();

            buff.pos(0.999, f, 0.001).color(r, g, b, a).tex(uMin, vMin).lightmap(l1, l2).endVertex();
            buff.pos(0.999, h, 0.001).color(r, g, b, a).tex(uMid, vMin).lightmap(l1, l2).endVertex();
            buff.pos(0.999, h, 0.999).color(r, g, b, a).tex(uMid, vMax).lightmap(l1, l2).endVertex();
            buff.pos(0.999, f, 0.999).color(r, g, b, a).tex(uMin, vMax).lightmap(l1, l2).endVertex();

            buff.pos(0.001, f, 0.999).color(r, g, b, a).tex(uMin, vMax).lightmap(l1, l2).endVertex();
            buff.pos(0.001, h, 0.999).color(r, g, b, a).tex(uMid, vMax).lightmap(l1, l2).endVertex();
            buff.pos(0.001, h, 0.001).color(r, g, b, a).tex(uMid, vMin).lightmap(l1, l2).endVertex();
            buff.pos(0.001, f, 0.001).color(r, g, b, a).tex(uMin, vMin).lightmap(l1, l2).endVertex();
            
            tess.draw();
            buff.setTranslation(0, 0, 0);
        }
        finally
        {
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
