package com.jtripled.simplefactory.fluid.tile;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

/**
 *
 * @author jtripled
 */
public class TileTank extends TileFluid
{
    public TileTank baseTank;
    
    public TileTank()
    {
        super(Fluid.BUCKET_VOLUME * 16);
        this.baseTank = null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T)getBaseTank() : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        if (getBaseTank() == this)
        {
            compound.setInteger("capacity", tank.getCapacity());
        }
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("capacity"))
        {
            tank.setCapacity(compound.getInteger("capacity"));
        }
        super.readFromNBT(compound);
    }
    
    public TileTank getBaseTank()
    {
        //if (baseTank == null)
        //{
            BlockPos next = pos;
            TileEntity test = world.getTileEntity(next);
            TileTank base = null;
            while (test instanceof TileTank && next.getY() >= 0)
            {
                base = (TileTank) test;
                next = next.down();
                test = world.getTileEntity(next);
            }
            baseTank = base;
        //}
        return baseTank;
    }
    
    @Override
    public FluidTank getInternalTank()
    {
        return getBaseTank().tank;
    }
    
    @Override
    public BlockPos getInternalTankPos()
    {
        return getBaseTank().pos;
    }
}
