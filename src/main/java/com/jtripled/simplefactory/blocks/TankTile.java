package com.jtripled.simplefactory.blocks;

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

/**
 *
 * @author jtripled
 */
public class TankTile extends TileEntity implements IFluidHandler
{
    protected FluidTank tank;
    protected TankTile baseTank;
    
    public TankTile()
    {
        this.tank = new FluidTank(16000);
        this.baseTank = null;
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
        if (getBaseTank() == this)
        {
            compound.setInteger("capacity", tank.getCapacity());
        }
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        tank.readFromNBT(compound);
        if (compound.hasKey("capacity"))
        {
            tank.setCapacity(compound.getInteger("capacity"));
        }
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
    
    public TankTile getBaseTank()
    {
        if (baseTank == null)
        {
            BlockPos next = pos;
            TileEntity test = world.getTileEntity(next);
            TankTile base = null;
            while (test != null && test instanceof TankTile && next.getY() >= 0)
            {
                base = (TankTile) test;
                next = next.down();
                test = world.getTileEntity(next);
            }
            baseTank = base;
        }
        return baseTank;
    }
    
    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return getBaseTank().getTankProperties();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        return getBaseTank().tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        return getBaseTank().tank.drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        return getBaseTank().tank.drain(maxDrain, doDrain);
    }
}
