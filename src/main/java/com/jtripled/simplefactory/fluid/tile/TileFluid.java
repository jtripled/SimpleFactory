package com.jtripled.simplefactory.fluid.tile;

import com.jtripled.simplefactory.fluid.network.FluidMessage;
import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.voxen.tile.ITransferable;
import javax.annotation.Nonnull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class TileFluid extends TileEntity implements IFluidHandler, ITransferable
{
    public FluidTank tank;
    private int transferCooldown;
    protected final ItemStackHandler input;
    protected final ItemStackHandler output;
    
    public TileFluid(int capacity)
    {
        this.tank = new FluidTank(capacity);
        this.transferCooldown = -1;
        if (this.hasBucketSlot())
        {
            this.input = new ItemStackHandler(1);
            this.output = new BucketInventoryHandler(this.input);
        }
        else
        {
            this.input = null;
            this.output = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        tank.writeToNBT(compound);
        this.writeTransferCooldown(compound);
        if (hasBucketSlot())
        {
            compound.setTag("input", input.serializeNBT());
            compound.setTag("output", output.serializeNBT());
        }
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        tank.readFromNBT(compound);
        this.readTransferCooldown(compound);
        if (compound.hasKey("input"))
        {
            input.deserializeNBT(compound.getCompoundTag("input"));
            output.deserializeNBT(compound.getCompoundTag("output"));
        }
        super.readFromNBT(compound);
    }
    
    public boolean hasBucketSlot()
    {
        return false;
    }
    
    public IItemHandler getBucketInput()
    {
        return input;
    }
    
    public IItemHandler getBucketOutput()
    {
        return output;
    }
    
    public FluidTank getInternalTank()
    {
        return tank;
    }
    
    public BlockPos getInternalTankPos()
    {
        return pos;
    }
    
    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return getInternalTank().getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        FluidTank internalTank = getInternalTank();
        int filled = internalTank.fill(resource, doFill);
        if (!world.isRemote && doFill && filled > 0)
        {
            SimpleFactory.NETWORK.sendToAll(new FluidMessage(getInternalTankPos(), internalTank.getFluid()));
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        FluidTank internalTank = getInternalTank();
        FluidStack drained = internalTank.drain(resource, doDrain);
        if (!world.isRemote && doDrain && drained != null)
        {
            SimpleFactory.NETWORK.sendToAll(new FluidMessage(getInternalTankPos(), internalTank.getFluid()));
        }
        return drained;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        FluidTank internalTank = getInternalTank();
        FluidStack drained = internalTank.drain(maxDrain, doDrain);
        if (!world.isRemote && doDrain && drained != null)
        {
            SimpleFactory.NETWORK.sendToAll(new FluidMessage(getInternalTankPos(), internalTank.getFluid()));
        }
        return drained;
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
    
    public static class BucketInventoryHandler extends ItemStackHandler
    {
        private final IItemHandler input;
        
        public BucketInventoryHandler(IItemHandler input)
        {
            super(1);
            this.input = input;
        }
        
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (!stack.isEmpty() && stack.getItem() == Items.BUCKET)
                return input.insertItem(0, stack, simulate);
            return stack;
        }
    }
}
