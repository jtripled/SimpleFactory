package com.jtripled.voxen.block;

import com.jtripled.voxen.gui.GUIBase;
import com.jtripled.voxen.item.ItemBase;
import com.jtripled.voxen.registry.VoxenRegistry;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author jtripled
 */
public interface BlockBase
{
    public default void registerBlock(VoxenRegistry registry)
    {
        registry.registerBlock(this);
        if (getTileClass() != null)
        {
            registry.registerTileEntity(this, getTileClass());
            TileEntitySpecialRenderer tesr = getTESR();
            if (tesr != null)
            {
                registry.registerTileRenderer(this, tesr);
            }
        }
        if (this instanceof GUIBase)
        {
            registry.registerGUI((GUIBase) this);
        }
        if (getIgnoredProperties() != null)
        {
            registry.registerIgnoredProperties(this, getIgnoredProperties());
        }
    }

    public default void registerItem(VoxenRegistry registry)
    {
        if (getItem() != null)
            registry.registerItem((Item) getItem());
    }

    public default void registerRenderer(VoxenRegistry registry)
    {
        if (getItem() != null)
            registry.registerItemRenderer(getItem(), ((Item) getItem()).getUnlocalizedName());
    }

    public default Class<? extends TileEntity> getTileClass()
    {
        return null;
    }

    public default ItemBase getItem()
    {
        return null;
    }

    public default TileEntitySpecialRenderer getTESR()
    {
        return null;
    }
    
    public default IProperty[] getIgnoredProperties()
    {
        return null;
    }
}
