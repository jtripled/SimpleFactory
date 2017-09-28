package com.jtripled.simplefactory.fluid.tile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 *
 * @author jtripled
 */
public class TilePump extends TileFluid implements ITickable
{
    public TilePump()
    {
        super(Fluid.BUCKET_VOLUME * 8);
    }
    
    @Override
    public void update()
    {
        updateTransfer();
    }
    
    @Override
    public boolean hasBucketSlot()
    {
        return true;
    }
    
    @Override
    public boolean transferOut()
    {
        TileEntity testTile = world.getTileEntity(pos.offset(EnumFacing.getFront(getBlockMetadata() & 7)));
        if (testTile != null && testTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            IFluidHandler nextInventory = testTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
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
                if (tileAbove != null && tileAbove.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
                {
                    IFluidHandler handler = tileAbove.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
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
