package com.jtripled.simplefactory.blocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author jtripled
 */
public class TankTESR extends TileEntitySpecialRenderer<TankTile>
{
    @Override
    public void render(TankTile tile, double x, double y, double z, float ticks, int destroy, float alpha)
    {
        try
        {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        }
        finally
        {
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
