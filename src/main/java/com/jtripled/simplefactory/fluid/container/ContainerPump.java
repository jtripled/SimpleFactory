package com.jtripled.simplefactory.fluid.container;

import com.jtripled.simplefactory.fluid.tile.TilePump;
import com.jtripled.voxen.container.ContainerTile;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 *
 * @author jtripled
 */
public class ContainerPump extends ContainerTile<TilePump>
{
    public ContainerPump(TilePump tile, InventoryPlayer inventory)
    {
        super(2, inventory, tile);
        addSlotToContainer(new SlotItemHandler(getTile().getBucketInput(), 0, 55, 36)
        {
            @Override
            public void onSlotChanged()
            {
                tile.markDirty();
            }
        });
        addSlotToContainer(new SlotItemHandler(getTile().getBucketOutput(), 0, 105, 36)
        {
            @Override
            public void onSlotChanged()
            {
                tile.markDirty();
            }
            
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }
        });
    }
}
