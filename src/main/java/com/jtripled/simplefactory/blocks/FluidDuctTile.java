package com.jtripled.simplefactory.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 *
 * @author jtripled
 */
public class FluidDuctTile extends TileEntity implements ITickable
{
    protected final FluidTank tank;
    private int transferCooldown;
    private long tickedGameTime;
    private EnumFacing previous;
    
    public FluidDuctTile()
    {
        this.tank = new FluidTank(1000);
        this.transferCooldown = -1;
        this.previous = EnumFacing.EAST;
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
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T)tank
                : super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        tank.writeToNBT(compound);
        compound.setInteger("transferCooldown", transferCooldown);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        tank.readFromNBT(compound);
        transferCooldown = compound.getInteger("transferCooldown");
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
    
    @Override
    public void update()
    {
        if (world != null && !world.isRemote)
        {
            --transferCooldown;
            tickedGameTime = world.getTotalWorldTime();

            if (transferCooldown <= 0)
            {
                transferCooldown = 0;
                doTransfer();
            }
        }
    }
    
    public boolean doTransfer()
    {
        if (world != null && !world.isRemote)
        {
            if (transferCooldown <= 0)
            {
                boolean flag = false;
                if (!this.isEmpty())
                    flag = transferOut();
                if (flag)
                {
                    transferCooldown = 8;
                    this.markDirty();
                    return true;
                }
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    public boolean isEmpty()
    {
        return tank.getFluidAmount() <= 0;
    }

    public boolean isFull()
    {
        return tank.getFluidAmount() >= 1000;
    }
    
    private static EnumFacing getNextFacing(EnumFacing previous, IBlockState state)
    {
        EnumFacing[] next;
        switch (previous)
        {
            case DOWN:
                next = new EnumFacing[] { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN }; break;
            case UP:
                next = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP }; break;
            case NORTH:
                next = new EnumFacing[] { EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH }; break;
            case SOUTH:
                next = new EnumFacing[] { EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH }; break;
            case WEST:
                next = new EnumFacing[] { EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST }; break;
            default:
                next = EnumFacing.values(); break;
        }
        for (EnumFacing face : next)
        {
            if (face != state.getValue(ItemDuctBlock.FACING))
            {
                switch (face)
                {
                    case DOWN:
                        if (state.getValue(ItemDuctBlock.DOWN)) return face; break;
                    case UP:
                        if (state.getValue(ItemDuctBlock.UP)) return face; break;
                    case NORTH:
                        if (state.getValue(ItemDuctBlock.NORTH)) return face; break;
                    case SOUTH:
                        if (state.getValue(ItemDuctBlock.SOUTH)) return face; break;
                    case WEST:
                        if (state.getValue(ItemDuctBlock.WEST)) return face; break;
                    default:
                        if (state.getValue(ItemDuctBlock.EAST)) return EnumFacing.EAST; break;
                }
            }
        }
        return null;
    }
    
    public boolean transferOut()
    {
        EnumFacing next = getNextFacing(previous, world.getBlockState(pos).getActualState(world, pos));
        if (next == null)
            return false;
        TileEntity testTile = world.getTileEntity(pos.offset(next));
        if (testTile != null && testTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            previous = next;
            IFluidHandler nextInventory = testTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            int amount = nextInventory.fill(tank.drain(400, false), false);
            if (!isEmpty() && amount > 0)
            {
                nextInventory.fill(tank.drain(amount, true), true);
                return true;
            }
            return false;
        }
        return false;
    }
}
