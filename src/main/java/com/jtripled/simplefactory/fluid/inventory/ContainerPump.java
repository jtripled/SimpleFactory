package com.jtripled.simplefactory.fluid.inventory;

import com.jtripled.simplefactory.fluid.tile.TilePump;
import com.jtripled.voxen.inventory.ContainerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.SlotItemHandler;

/**
 *
 * @author jtripled
 */
public class ContainerPump extends ContainerBase
{
    protected final TilePump tile;
    
    public ContainerPump(TileEntity tile, InventoryPlayer inventory)
    {
        super(2, inventory);
        this.tile = (TilePump) tile;
        addSlotToContainer(new SlotItemHandler(this.tile.getBucketInput(), 0, 55, 36)
        {
            @Override
            public void onSlotChanged()
            {
                tile.markDirty();
            }
        });
        addSlotToContainer(new SlotItemHandler(this.tile.getBucketOutput(), 0, 105, 36)
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
    
    public TilePump getTile()
    {
        return tile;
    }
}
