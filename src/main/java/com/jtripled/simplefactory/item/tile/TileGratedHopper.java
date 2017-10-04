package com.jtripled.simplefactory.item.tile;

import com.jtripled.simplefactory.item.block.BlockGratedHopper;
import com.jtripled.voxen.tile.ITransferable;
import com.jtripled.voxen.tile.TileBase;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class TileGratedHopper extends TileBase implements ITransferable
{
    public final ItemStackHandler filter;
    protected ItemStackHandler inventory;
    private int transferCooldown;
    
    public TileGratedHopper()
    {
        TileEntity tile = this;
        this.filter = new ItemStackHandler(5) {
            @Override
            public int getSlotLimit(int slot)
            {
                return 1;
            }
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                this.setStackInSlot(slot, stack);
                tile.markDirty();
                return stack;
            }
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate)
            {
                this.setStackInSlot(slot, ItemStack.EMPTY);
                tile.markDirty();
                return ItemStack.EMPTY;
            }
        };
        this.inventory = new ItemStackHandler(5) {
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
        };
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.UP);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.UP)
                ? (T)inventory : null;
    }
    
    public IItemHandler getInventory()
    {
        return inventory;
    }
    
    public IItemHandler getFilter()
    {
        return filter;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        writeTransferCooldown(compound);
        compound.setTag("filter", filter.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        readTransferCooldown(compound);
        filter.deserializeNBT(compound.getCompoundTag("filter"));
        super.readFromNBT(compound);
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
    public boolean transferOut()
    {
        EnumFacing face = BlockGratedHopper.getFacing(this.getBlockMetadata());
        TileEntity testTile = world.getTileEntity(pos.offset(face));
        if (testTile != null && testTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()))
        {
            IItemHandler nextInventory = testTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
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
                            if (nextInventory.insertItem(j, inventory.extractItem(i, 1, true), true) == ItemStack.EMPTY)
                            {
                                nextInventory.insertItem(j, inventory.extractItem(i, 1, false), false);
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public boolean transferIn()
    {
        TileEntity testTile = world.getTileEntity(pos.up());
        if (testTile != null && testTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
        {
            IItemHandler handler = testTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
            ItemStack inStack;
            for (int i = 0; i < inventory.getSlots(); i++)
            {
                inStack = inventory.getStackInSlot(i);
                if (inStack.isEmpty() || inStack.getCount() < inStack.getMaxStackSize())
                {
                    ItemStack outStack;
                    for (int j = 0; j < handler.getSlots(); j++)
                    {
                        outStack = handler.getStackInSlot(j);
                        if (!outStack.isEmpty() && (inStack.isEmpty() || inStack.getItem() == outStack.getItem()))
                        {
                            ItemStack test = inventory.insertItem(i, handler.extractItem(j, 1, true), true);
                            if (test.isEmpty())
                            {
                                inventory.insertItem(i, handler.extractItem(j, 1, false), false);
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }
}
