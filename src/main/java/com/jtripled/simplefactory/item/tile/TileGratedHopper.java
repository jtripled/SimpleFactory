package com.jtripled.simplefactory.item.tile;

import com.jtripled.simplefactory.item.block.BlockGratedHopper;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
public class TileGratedHopper extends TileItem implements ITickable
{
    public final ItemStackHandler filter;
    
    public TileGratedHopper()
    {
        this.filter = new ItemStackHandler(5) {
            @Override
            public int getSlotLimit(int slot)
            {
                return 1;
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
    
    @Override
    public boolean hasFilter()
    {
        return true;
    }
    
    @Override
    public IItemHandler getFilter()
    {
        return filter;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("filter", filter.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        filter.deserializeNBT(compound.getCompoundTag("filter"));
        super.readFromNBT(compound);
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
        if (!this.isFull() && testTile != null && testTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN))
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
