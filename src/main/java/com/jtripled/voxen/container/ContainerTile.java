package com.jtripled.voxen.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author jtripled
 */
public class ContainerTile<T extends TileEntity> extends ContainerBase
{
    private final T tile;
    
    public ContainerTile(float rows, InventoryPlayer inventory, T tile)
    {
        super(rows, inventory);
        this.tile = tile;
    }
    
    public T getTile()
    {
        return tile;
    }
}
