package com.jtripled.simplefactory.fluid;

import com.jtripled.simplefactory.fluid.network.FluidMessage;
import com.jtripled.simplefactory.SimpleFactory;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;

/**
 *
 * @author jtripled
 */
public class TileFluid extends TileEntity implements IFluidHandler
{
    protected FluidTank tank;
    
    public TileFluid(int capacity)
    {
        this.tank = new FluidTank(capacity);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T)this
                : super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        tank.writeToNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        tank.readFromNBT(compound);
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
    
    public boolean hasBucketSlot()
    {
        return false;
    }
    
    public IItemHandler getBucketInput()
    {
        return null;
    }
    
    public IItemHandler getBucketOutput()
    {
        return null;
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
}
