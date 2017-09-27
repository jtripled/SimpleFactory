package com.jtripled.simplefactory.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 *
 * @author jtripled
 */
public class FluidDuctContainer extends Container
{
    protected final FluidDuctTile tile;
    
    public FluidDuctContainer(FluidDuctTile tile, InventoryPlayer playerInv)
    {
        this.tile = tile;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 50 + i * 18));
        for (int k = 0; k < 9; k++)
            addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 108));
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }
    
    public FluidDuctTile getTile()
    {
        return tile;
    }
}
