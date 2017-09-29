package com.jtripled.simplefactory.item.tile;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class TileItem extends TileEntity implements ITickable
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
        compound.setInteger("transferCooldown", transferCooldown);
        compound.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        transferCooldown = compound.getInteger("transferCooldown");
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
    }
    
    @Override
    public void onDataPacket(NetworkManager network, SPacketUpdateTileEntity packet)
    {
        readFromNBT(packet.getNbtCompound());
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }
    
    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(super.getUpdateTag());
    }
    
    @Override
    public void handleUpdateTag(NBTTagCompound compound)
    {
        readFromNBT(compound);
    }
    
    @Override
    public void update()
    {
        if (world != null && !world.isRemote)
        {
            --transferCooldown;
            if (transferCooldown <= 0)
            {
                transferCooldown = 0;
                boolean flag = false;
                if (!this.isEmpty())
                    flag = transferOut();
                if (!this.isFull())
                    flag = transferIn() || flag;
                if (flag)
                {
                    transferCooldown = 8;
                    this.markDirty();
                }
            }
        }
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
    
    public boolean transferOut()
    {
        return false;
    }
    
    public boolean transferIn()
    {
        return false;
    }
}
