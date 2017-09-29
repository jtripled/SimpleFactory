package com.jtripled.simplefactory.item.tile;

import com.jtripled.voxen.tile.ITransferable;
import com.jtripled.voxen.tile.TileBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class TileItem extends TileBase implements ITransferable
{
    protected ItemStackHandler inventory;
    private int transferCooldown;
    
    public TileItem()
    {
        this.inventory = null;
        this.transferCooldown = -1;
    }
    
    public boolean hasFilter()
    {
        return false;
    }
    
    public IItemHandler getInventory()
    {
        return inventory;
    }
    
    public IItemHandler getFilter()
    {
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        writeTransferCooldown(compound);
        compound.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        readTransferCooldown(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
    }

    public boolean isEmpty()
    {
        for (int i = 0; i < inventory.getSlots(); i++)
            if (!inventory.getStackInSlot(i).isEmpty())
                return false;
        return true;
    }

    public boolean isFull()
    {
        ItemStack stack;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            stack = inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize())
                return false;
        }
        return true;
    }
    
    @Override
    public boolean canTransferOut()
    {
        return !isEmpty();
    }
    
    @Override
    public boolean canTransferIn()
    {
        return !isFull();
    }

    @Override
    public int getTransferCooldown()
    {
        return transferCooldown;
    }

    @Override
    public void setTransferCooldown(int cooldown)
    {
        transferCooldown = cooldown;
    }
}
