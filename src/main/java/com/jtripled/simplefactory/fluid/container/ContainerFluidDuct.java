package com.jtripled.simplefactory.fluid.container;

import com.jtripled.simplefactory.fluid.tile.TileFluidDuct;
import com.jtripled.voxen.container.ContainerTile;
import net.minecraft.entity.player.InventoryPlayer;

/**
 *
 * @author jtripled
 */
public class ContainerFluidDuct extends ContainerTile<TileFluidDuct>
{
    public ContainerFluidDuct(TileFluidDuct tile, InventoryPlayer inventory)
    {
        super(1, inventory, tile);
    }
}
