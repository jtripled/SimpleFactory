package com.jtripled.simplefactory.fluid.tile;

import com.jtripled.simplefactory.fluid.network.FluidMessage;
import com.jtripled.simplefactory.SimpleFactory;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class TileFluid extends TileEntity implements IFluidHandler
{
    public FluidTank tank;
    private int transferCooldown;
    protected final ItemStackHandler input;
    protected final ItemStackHandler output;
    
    public TileFluid(int capacity)
    {
        this.tank = new FluidTank(capacity);
        this.transferCooldown = -1;
        if (this.hasBucketSlot())
        {
            this.input = new ItemStackHandler(1);
            this.output = new BucketInventoryHandler(this.input);
        }
        else
        {
            this.input = null;
            this.output = null;
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.hasBucketSlot())
                || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T)this
                : capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.hasBucketSlot() ? (T)input
                : super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        tank.writeToNBT(compound);
        if (transferCooldown != -1)
            compound.setInteger("transferCooldown", transferCooldown);
        if (hasBucketSlot())
        {
            compound.setTag("input", input.serializeNBT());
            compound.setTag("output", output.serializeNBT());
        }
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        tank.readFromNBT(compound);
        if (compound.hasKey("transferCooldown"))
            transferCooldown = compound.getInteger("transferCooldown");
        if (compound.hasKey("input"))
        {
            input.deserializeNBT(compound.getCompoundTag("input"));
            output.deserializeNBT(compound.getCompoundTag("output"));
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
    
    public boolean hasBucketSlot()
    {
        return false;
    }
    
    public IItemHandler getBucketInput()
    {
        return input;
    }
    
    public IItemHandler getBucketOutput()
    {
        return output;
    }
    
    public FluidTank getInternalTank()
    {
        return tank;
    }
    
    public BlockPos getInternalTankPos()
    {
        return pos;
    }
    
    public void updateTransfer()
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
                if (tank.getFluidAmount() < tank.getCapacity())
                    flag = transferIn() || flag;
                if (flag)
                {
                    transferCooldown = 8;
                    this.markDirty();
                }
            }
        }
    }
    
    public boolean transferOut()
    {
        return false;
    }
    
    public boolean transferIn()
    {
        return false;
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
    
    public static class BucketInventoryHandler extends ItemStackHandler
    {
        private final IItemHandler input;
        
        public BucketInventoryHandler(IItemHandler input)
        {
            super(1);
            this.input = input;
        }
        
        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (!stack.isEmpty() && stack.getItem() == Items.BUCKET)
                return input.insertItem(0, stack, simulate);
            return stack;
        }
    }
}
