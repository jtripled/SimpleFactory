package com.jtripled.simplefactory.item.tile;

import com.jtripled.voxen.block.IBlockDuct;
import com.jtripled.voxen.tile.ITransferable;
import com.jtripled.voxen.tile.TileBase;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class TileItemDuct extends TileBase implements ITransferable
{
    private final ItemStackHandler inventory;
    private EnumFacing previous;
    private int transferCooldown;
    
    public TileItemDuct()
    {
        this.previous = EnumFacing.EAST;
        this.inventory = new ItemStackHandler(1);
        this.transferCooldown = -1;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == getFacing(this) || facing == null);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == getFacing(this) || facing == null)
                ? (T)inventory : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        writeTransferCooldown(compound);
        compound.setInteger("previous", previous.getIndex());
        compound.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        readTransferCooldown(compound);
        previous = EnumFacing.getFront(compound.getInteger("previous"));
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
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
        return !inventory.getStackInSlot(0).isEmpty();
    }
    
    @Override
    public boolean transferOut()
    {
        EnumFacing next = getNextFacing(previous, world.getBlockState(pos).getActualState(world, pos));
        if (next == null)
            return false;
        TileEntity testTile = world.getTileEntity(pos.offset(next));
        if (testTile != null && testTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, next.getOpposite()))
        {
            previous = next;
            IItemHandler nextInventory = testTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, next.getOpposite());
            ItemStack outStack = inventory.getStackInSlot(0);
            if (!outStack.isEmpty())
            {
                ItemStack inStack;
                for (int i = 0; i < nextInventory.getSlots(); i++)
                {
                    inStack = nextInventory.getStackInSlot(i);
                    if (inStack.isEmpty() || (inStack.getCount() < inStack.getMaxStackSize()
                            && outStack.getItem() == inStack.getItem()))
                    {
                        if (nextInventory.insertItem(i, inventory.extractItem(0, 1, true), true) == ItemStack.EMPTY)
                        {
                            nextInventory.insertItem(i, inventory.extractItem(0, 1, false), false);
                            return true;
                        }
                    }
                }
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
            if (face != state.getValue(IBlockDuct.FACING))
            {
                switch (face)
                {
                    case DOWN: if (state.getValue(IBlockDuct.DOWN)) return face; break;
                    case UP: if (state.getValue(IBlockDuct.UP)) return face; break;
                    case NORTH: if (state.getValue(IBlockDuct.NORTH)) return face; break;
                    case SOUTH: if (state.getValue(IBlockDuct.SOUTH)) return face; break;
                    case WEST: if (state.getValue(IBlockDuct.WEST)) return face; break;
                    default: if (state.getValue(IBlockDuct.EAST)) return EnumFacing.EAST; break;
                }
            }
        }
        return null;
    }
    
    public static EnumFacing getFacing(TileItemDuct tile)
    {
        return tile.world.getBlockState(tile.getPos()).getValue(IBlockDuct.FACING);
    }
}
