package com.jtripled.simplefactory.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 *
 * @author jtripled
 */
public class GratedHopperContainer extends Container
{
    protected final GratedHopperTile tile;
    
    public GratedHopperContainer(GratedHopperTile tile, InventoryPlayer playerInv)
    {
        this.tile = tile;
        for (int i = 0; i < 5; i++)
        {
            addSlotToContainer(new SlotItemHandler(tile.inventory, i, 44 + i * 18, 18) {
                @Override
                public void onSlotChanged() {
                    tile.markDirty();
                }
            });
        }
        for (int i = 0; i < 5; i++)
        {
            addSlotToContainer(new SlotItemHandler(tile.filter, i, 44 + i * 18, 45) {
                @Override
                public void onSlotChanged() {
                    tile.markDirty();
                }
            });
        }
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 77 + i * 18));
        for (int k = 0; k < 9; k++)
            addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 135));
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 10)
            {
                if (!this.mergeItemStack(itemstack1, 10, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 5, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }
}
