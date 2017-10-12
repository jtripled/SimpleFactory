package com.jtripled.simplefactory.item.gui;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.item.container.ContainerItemDuct;
import com.jtripled.voxen.gui.GUIContainerTile;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author jtripled
 */
public class GUIItemDuct extends GUIContainerTile<ContainerItemDuct>
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(SimpleFactory.ID, "textures/gui/item_duct.png");

    public GUIItemDuct(ContainerItemDuct container)
    {
        super(container);
        this.ySize = 132;
    }

    @Override
    public ResourceLocation getTexture()
    {
        return TEXTURE;
    }
}
