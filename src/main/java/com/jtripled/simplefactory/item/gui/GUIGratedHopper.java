package com.jtripled.simplefactory.item.gui;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.item.container.ContainerGratedHopper;
import com.jtripled.voxen.gui.GUIContainerTile;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author jtripled
 */
public class GUIGratedHopper extends GUIContainerTile<ContainerGratedHopper>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(SimpleFactory.ID, "textures/gui/grated_hopper.png");

    public GUIGratedHopper(ContainerGratedHopper container)
    {
        super(container);
        this.ySize = 159;
    }

    @Override
    public ResourceLocation getTexture()
    {
        return TEXTURE;
    }
}
