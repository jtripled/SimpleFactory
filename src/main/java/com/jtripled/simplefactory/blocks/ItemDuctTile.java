package com.jtripled.simplefactory.blocks;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class ItemDuctTile extends TileEntity implements ITickable
{
    private final ItemStackHandler inventory;
    private int transferCooldown;
    private long tickedGameTime;
    private EnumFacing previous;
    
    public ItemDuctTile()
    {
        this.inventory = new ItemStackHandler(1);
        this.transferCooldown = -1;
        this.previous = EnumFacing.EAST;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                ? (T)inventory : super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setInteger("transferCooldown", transferCooldown);
        compound.setInteger("previous", previous.getIndex());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        transferCooldown = compound.getInteger("transferCooldown");
        previous = EnumFacing.getFront(compound.getInteger("previous"));
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
        return inventory.getStackInSlot(0).isEmpty();
    }

    public boolean isFull()
    {
        ItemStack stack = inventory.getStackInSlot(0);
        return stack.isEmpty() || stack.getCount() < stack.getMaxStackSize();
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
        if (testTile != null && testTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
        {
            previous = next;
            IItemHandler nextInventory = testTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
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
                        nextInventory.insertItem(i, inventory.extractItem(0, 1, false), false);
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
}
