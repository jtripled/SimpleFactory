package com.jtripled.voxen.block;

import com.jtripled.voxen.item.ItemBase;
import com.jtripled.voxen.registry.RegistrationHandler;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import com.jtripled.voxen.gui.GUIHolder;

/**
 *
 * @author jtripled
 */
public interface IBlockBase
{
    public String getName();

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
    
    public default void registerBlock(RegistrationHandler registry)
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
        if (this instanceof GUIHolder)
        {
            registry.registerGUI((GUIHolder) this);
        }
        if (getIgnoredProperties() != null)
        {
            registry.registerIgnoredProperties(this, getIgnoredProperties());
        }
    }

    public default void registerItem(RegistrationHandler registry)
    {
        if (getItem() != null)
            registry.registerItem(getItem());
    }

    public default void registerRenderer(RegistrationHandler registry)
    {
        if (getItem() != null)
            registry.registerItemRenderer(getItem(), getName());
    }
}
