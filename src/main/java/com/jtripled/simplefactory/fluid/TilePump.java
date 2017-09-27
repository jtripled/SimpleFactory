package com.jtripled.simplefactory.fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class TilePump extends TileFluid implements ITickable
{
    protected final ItemStackHandler input;
    protected final ItemStackHandler output;
    private int transferCooldown;
    private long tickedGameTime;
    
    public TilePump()
    {
        super(Fluid.BUCKET_VOLUME * 8);
        this.input = new ItemStackHandler(1);
        this.output = new PumpInventoryHandler(this.input);
        this.transferCooldown = -1;
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
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T)input
                : super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("input", input.serializeNBT());
        compound.setTag("output", output.serializeNBT());
        compound.setInteger("transferCooldown", transferCooldown);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        input.deserializeNBT(compound.getCompoundTag("input"));
        output.deserializeNBT(compound.getCompoundTag("output"));
        transferCooldown = compound.getInteger("transferCooldown");
        super.readFromNBT(compound);
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
                if (!this.isFull())
                    flag = transferIn() || flag;
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
        return tank.getFluidAmount() >= tank.getCapacity();
    }
    
    @Override
    public boolean hasBucketSlot()
    {
        return true;
    }
    
    @Override
    public IItemHandler getBucketInput()
    {
        return input;
    }
    
    @Override
    public IItemHandler getBucketOutput()
    {
        return output;
    }
    
    public boolean transferOut()
    {
        TileEntity testTile = world.getTileEntity(pos.offset(BlockPump.getFacing(getBlockMetadata())));
        if (testTile != null && testTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            IFluidHandler nextInventory = testTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            int amount = nextInventory.fill(drain(400, false), false);
            if (!isEmpty() && amount > 0)
            {
                nextInventory.fill(drain(amount, true), true);
                return true;
            }
            return false;
        }
        return false;
    }
    
    public boolean transferIn()
    {
        if (!isFull())
        {
            BlockPos above = pos.up();
            if (world.getBlockState(above).getBlock() instanceof BlockLiquid)
            {
                BlockLiquid block = (BlockLiquid) world.getBlockState(above).getBlock();
                Fluid fromFluid = FluidRegistry.lookupFluidForBlock(block);
                if (fromFluid == FluidRegistry.WATER)
                {
                    if (tank.getFluid() == null || tank.getFluid().getFluid() == fromFluid)
                    {
                        fill(new FluidStack(fromFluid, 100), true);
                        return true;
                    }
                }
                else if (fromFluid != null && tank.getFluidAmount() <= tank.getCapacity() - Fluid.BUCKET_VOLUME)
                {
                    if (block.getMetaFromState(world.getBlockState(above)) == 0 && (tank.getFluid() == null || tank.getFluid().getFluid() == fromFluid))
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
    
    public static class PumpInventoryHandler extends ItemStackHandler
    {
        private final IItemHandler input;
        
        public PumpInventoryHandler(IItemHandler input)
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
