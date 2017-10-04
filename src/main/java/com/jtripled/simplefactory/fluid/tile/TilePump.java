package com.jtripled.simplefactory.fluid.tile;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 *
 * @author jtripled
 */
public class TilePump extends TileFluid
{
    public TilePump()
    {
        super(Fluid.BUCKET_VOLUME * 8);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == EnumFacing.UP;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == EnumFacing.UP ? (T)this : null;
    }
    
    @Override
    public boolean hasBucketSlot()
    {
        return true;
    }
    
    @Override
    public boolean canTransferOut()
    {
        return this.getInternalTank().getFluidAmount() > 0;
    }
    
    @Override
    public boolean canTransferIn()
    {
        FluidTank internal = this.getInternalTank();
        return internal.getFluidAmount() < internal.getCapacity();
    }
    
    @Override
    public boolean transferOut()
    {
        EnumFacing face = EnumFacing.getFront(getBlockMetadata() & 7);
        TileEntity testTile = world.getTileEntity(pos.offset(face));
        if (testTile != null && testTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite()))
        {
            IFluidHandler nextInventory = testTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
            int amount = nextInventory.fill(drain(400, false), false);
            if (tank.getFluidAmount() > 0 && amount > 0)
            {
                nextInventory.fill(drain(amount, true), true);
                return true;
            }
            return false;
        }
        return false;
    }
    
    @Override
    public boolean transferIn()
    {
        if (tank.getFluidAmount() < tank.getCapacity())
        {
            BlockPos above = pos.up();
            Block aboveBlock = world.getBlockState(above).getBlock();
            if (aboveBlock instanceof BlockLiquid || aboveBlock instanceof BlockFluidBase)
            {
                Fluid fromFluid = FluidRegistry.lookupFluidForBlock(aboveBlock);
                if (fromFluid == FluidRegistry.WATER)
                {
                    if (tank.getFluid() == null || tank.getFluid().getFluid() == fromFluid)
                    {
                        fill(new FluidStack(fromFluid, 1000), true);
                        return true;
                    }
                }
                else if (fromFluid != null && tank.getFluidAmount() <= tank.getCapacity() - Fluid.BUCKET_VOLUME)
                {
                    if ((aboveBlock.getMetaFromState(world.getBlockState(above)) == 0
                            && (tank.getFluid() == null || tank.getFluid().getFluid() == fromFluid))
                            || (aboveBlock instanceof BlockFluidBase && aboveBlock.getMetaFromState(world.getBlockState(above)) == 15))
                    {
                        world.setBlockToAir(above);
                        fill(new FluidStack(fromFluid, Fluid.BUCKET_VOLUME), true);
                        return true;
                    }
                }
            }
            else
            {
                TileEntity tileAbove = world.getTileEntity(above);
                if (tileAbove != null && tileAbove.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN))
                {
                    IFluidHandler handler = tileAbove.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
                    FluidStack drained = handler.drain(400, false);
                    if (drained != null && (tank.getFluid() == null || drained.getFluid() == tank.getFluid().getFluid()))
                    {
                        int amount = fill(drained, false);
                        fill(handler.drain(amount, true), true);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
