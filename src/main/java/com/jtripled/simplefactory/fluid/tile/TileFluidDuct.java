package com.jtripled.simplefactory.fluid.tile;

import com.jtripled.simplefactory.fluid.block.BlockFluidDuct;
import com.jtripled.simplefactory.item.block.BlockItemDuct;
import com.jtripled.voxen.tile.IFluidTank;
import com.jtripled.voxen.tile.ITransferable;
import com.jtripled.voxen.tile.TileBase;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 *
 * @author jtripled
 */
public class TileFluidDuct extends TileBase implements IFluidTank, ITransferable
{
    private FluidTank tank;
    private EnumFacing previous;
    private int transferCooldown;
    
    public TileFluidDuct()
    {
        this.tank = new FluidTank(Fluid.BUCKET_VOLUME * 1);
        this.previous = EnumFacing.EAST;
        this.transferCooldown = -1;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == getFacing(this) || facing == null);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == getFacing(this) || facing == null) ? (T)this : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        writeInternalTank(compound);
        compound.setInteger("previous", previous.getIndex());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        readInternalTank(compound);
        previous = EnumFacing.getFront(compound.getInteger("previous"));
        super.readFromNBT(compound);
    }

    @Override
    public FluidTank getInternalTank()
    {
        return tank;
    }

    @Override
    public BlockPos getInternalTankPos()
    {
        return pos;
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
    
    @Override
    public boolean canTransferOut()
    {
        return this.getInternalTank().getFluidAmount() > 0;
    }
    
    @Override
    public boolean transferOut()
    {
        EnumFacing next = getNextFacing(previous, world.getBlockState(pos).getActualState(world, pos));
        if (next == null)
            return false;
        TileEntity testTile = world.getTileEntity(pos.offset(next));
        if (testTile != null && testTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, next.getOpposite()))
        {
            previous = next;
            IFluidHandler nextInventory = testTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, next.getOpposite());
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
    
    private static EnumFacing getNextFacing(EnumFacing previous, IBlockState state)
    {
        EnumFacing[] next;
        switch (previous)
        {
            case DOWN: next = new EnumFacing[] { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN }; break;
            case UP: next = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP }; break;
            case NORTH: next = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH }; break;
            case SOUTH: next = new EnumFacing[] { EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH }; break;
            case WEST: next = new EnumFacing[] { EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST }; break;
            default: next = EnumFacing.values(); break;
        }
        for (EnumFacing face : next)
        {
            if (face != state.getValue(BlockFluidDuct.FACING))
            {
                switch (face)
                {
                    case DOWN: if (state.getValue(BlockItemDuct.DOWN)) return face; break;
                    case UP: if (state.getValue(BlockItemDuct.UP)) return face; break;
                    case NORTH: if (state.getValue(BlockItemDuct.NORTH)) return face; break;
                    case SOUTH: if (state.getValue(BlockItemDuct.SOUTH)) return face; break;
                    case WEST: if (state.getValue(BlockItemDuct.WEST)) return face; break;
                    default: if (state.getValue(BlockItemDuct.EAST)) return EnumFacing.EAST; break;
                }
            }
        }
        return null;
    }
    
    public static EnumFacing getFacing(TileFluidDuct tile)
    {
        return tile.world.getBlockState(tile.getPos()).getValue(BlockFluidDuct.FACING);
    }
}
