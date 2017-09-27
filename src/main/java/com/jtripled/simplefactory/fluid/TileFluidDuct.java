package com.jtripled.simplefactory.fluid;

import com.jtripled.simplefactory.blocks.ItemDuctBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 *
 * @author jtripled
 */
public class TileFluidDuct extends TileFluid implements ITickable
{
    private int transferCooldown;
    private EnumFacing previous;
    
    public TileFluidDuct()
    {
        super(Fluid.BUCKET_VOLUME * 1);
        this.transferCooldown = -1;
        this.previous = EnumFacing.EAST;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger("transferCooldown", transferCooldown);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        transferCooldown = compound.getInteger("transferCooldown");
        super.readFromNBT(compound);
    }
    
    @Override
    public void update()
    {
        if (world != null && !world.isRemote)
        {
            --transferCooldown;
            if (transferCooldown <= 0)
            {
                transferCooldown = 0;
                boolean flag = false;
                if (tank.getFluidAmount() > 0)
                    flag = transferOut();
                if (flag)
                {
                    transferCooldown = 8;
                    this.markDirty();
                }
            }
        }
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
}
