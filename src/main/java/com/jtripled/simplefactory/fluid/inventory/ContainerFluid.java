package com.jtripled.simplefactory.fluid.inventory;

import com.jtripled.simplefactory.fluid.tile.TileFluid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 *
 * @author jtripled
 */
public class ContainerFluid extends Container
{
    protected final TileFluid tile;
    
    public ContainerFluid(TileFluid tile, InventoryPlayer inventory)
    {
        this.tile = tile;
        int offset = 0;
        if (tile.hasBucketSlot())
        {
            offset = 18;
            IItemHandler input = tile.getBucketInput();
            IItemHandler output = tile.getBucketOutput();
            addSlotToContainer(new SlotItemHandler(input, 0, 55, 36) {
                @Override
                public void onSlotChanged() {
                    tile.markDirty();
                }
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return stack.getItem() == Items.BUCKET;
                }
            });
            addSlotToContainer(new SlotItemHandler(output, 0, 105, 36) {
                @Override
                public void onSlotChanged() {
                    tile.markDirty();
                }
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return false;
                }
            });
        }
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 50 + i * 18 + offset));
        for (int k = 0; k < 9; k++)
            addSlotToContainer(new Slot(inventory, k, 8 + k * 18, 108 + offset));
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        if (tile.hasBucketSlot())
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
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }
    
    public TileFluid getTile()
    {
        return tile;
    }
}
