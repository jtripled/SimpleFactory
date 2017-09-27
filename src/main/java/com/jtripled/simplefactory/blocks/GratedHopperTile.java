package com.jtripled.simplefactory.blocks;

import javax.annotation.Nonnull;
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
public class GratedHopperTile extends TileEntity implements ITickable
{
    protected final ItemStackHandler filter;
    protected final ItemStackHandler inventory;
    private int transferCooldown;
    private long tickedGameTime;
    
    public GratedHopperTile()
    {
        this.filter = new GratedHopperFilterHandler();
        this.inventory = new GratedHopperInventoryHandler(this.filter);
        this.transferCooldown = -1;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                ? (T)inventory : super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setTag("filter", filter.serializeNBT());
        compound.setInteger("transferCooldown", transferCooldown);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        filter.deserializeNBT(compound.getCompoundTag("filter"));
        transferCooldown = compound.getInteger("transferCooldown");
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
            tickedGameTime = world.getTotalWorldTime();

            if (transferCooldown <= 0)
            {
                transferCooldown = 0;
                doTransfer();
            }
        }
    }
    
    public boolean doTransfer()
    {
        if (world != null && !world.isRemote)
        {
            if (transferCooldown <= 0)
            {
                boolean flag = false;
                if (!this.isEmpty())
                    flag = transferOut();
                if (!this.isFull())
                    flag = transferIn() || flag;
                if (flag)
                {
                    transferCooldown = 8;
                    this.markDirty();
                    return true;
                }
            }
            return false;
        }
        else
        {
            return false;
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
        TileEntity testTile = world.getTileEntity(pos.offset(GratedHopperBlock.getFacing(this.getBlockMetadata())));
        if (testTile != null && testTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
        {
            IItemHandler nextInventory = testTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            for (int i = 0; i < inventory.getSlots(); i++)
            {
                ItemStack outStack = inventory.getStackInSlot(i);
                if (!outStack.isEmpty())
                {
                    ItemStack inStack;
                    for (int j = 0; j < nextInventory.getSlots(); j++)
                    {
                        inStack = nextInventory.getStackInSlot(j);
                        if (inStack.isEmpty() || (inStack.getCount() < inStack.getMaxStackSize()
                                && outStack.getItem() == inStack.getItem()))
                        {
                            nextInventory.insertItem(j, inventory.extractItem(i, 1, false), false);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }
    
    public boolean transferIn()
    {
        return true;
    }
    
    public static class GratedHopperFilterHandler extends ItemStackHandler
    {
        public GratedHopperFilterHandler()
        {
            super(5);
        }
        
        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }
    }
    
    public static class GratedHopperInventoryHandler extends ItemStackHandler
    {
        private final IItemHandler filter;
        
        public GratedHopperInventoryHandler(IItemHandler filter)
        {
            super(5);
            this.filter = filter;
        }
        
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            ItemStack compare = new ItemStack(stack.getItem(), 1, stack.getMetadata());
            for (int i = 0; i < filter.getSlots(); i++)
                if (ItemStack.areItemStacksEqual(compare, filter.getStackInSlot(i)))
                    return super.insertItem(slot, stack, simulate);
            return stack;
        }
    }
}
