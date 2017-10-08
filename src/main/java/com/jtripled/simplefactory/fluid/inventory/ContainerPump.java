package com.jtripled.simplefactory.fluid.inventory;

import com.jtripled.simplefactory.fluid.tile.TilePump;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 *
 * @author jtripled
 */
public class ContainerPump extends Container
{
    protected final TilePump tile;
    
    public ContainerPump(TilePump tile, InventoryPlayer inventory)
    {
        this.tile = tile;
        addSlotToContainer(new SlotItemHandler(tile.getBucketInput(), 0, 55, 36) {
            @Override
            public void onSlotChanged() {
                tile.markDirty();
            }
        });
        addSlotToContainer(new SlotItemHandler(tile.getBucketOutput(), 0, 105, 36) {
            @Override
            public void onSlotChanged() {
                tile.markDirty();
            }
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        });
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 68 + i * 18));
        for (int k = 0; k < 9; k++)
            addSlotToContainer(new Slot(inventory, k, 8 + k * 18, 126));
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

            if (index < 2)
            {
                if (!this.mergeItemStack(itemstack1, 2, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 1, false))
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
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }
    
    public TilePump getTile()
    {
        return tile;
    }
}
