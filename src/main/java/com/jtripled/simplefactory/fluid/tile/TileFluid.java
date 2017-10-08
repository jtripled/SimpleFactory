package com.jtripled.simplefactory.fluid.tile;

import com.jtripled.simplefactory.fluid.network.FluidMessage;
import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.voxen.tile.ITransferable;
import com.jtripled.voxen.tile.TileBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 *
 * @author jtripled
 */
public class TileFluid extends TileBase implements IFluidHandler, ITransferable
{
    public FluidTank tank;
    private int transferCooldown;
    
    public TileFluid(int capacity)
    {
        this.tank = new FluidTank(capacity);
        this.transferCooldown = -1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        tank.writeToNBT(compound);
        this.writeTransferCooldown(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        tank.readFromNBT(compound);
        this.readTransferCooldown(compound);
        super.readFromNBT(compound);
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
}
