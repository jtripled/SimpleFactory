package com.jtripled.simplefactory.fluid.tile;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.fluid.network.BucketCooldownMessage;
import com.jtripled.voxen.tile.IFluidTank;
import com.jtripled.voxen.tile.ITransferable;
import com.jtripled.voxen.tile.TileBase;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 *
 * @author jtripled
 */
public class TilePump extends TileBase implements IFluidTank, ITransferable
{
    private FluidTank tank;
    private int transferCooldown;
    private int bucketCooldown;
    private final ItemStackHandler input;
    private final ItemStackHandler output;
    
    public TilePump()
    {
        this.tank = new FluidTank(Fluid.BUCKET_VOLUME * 8);
        this.transferCooldown = -1;
        this.bucketCooldown = 25;
        this.input = new ItemStackHandler(1) {
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                Item item = stack.getItem();
                if (item == Items.BUCKET || item instanceof ItemBucket || item instanceof UniversalBucket)
                    return super.insertItem(slot, stack, simulate);
                return stack;
            }
        };
        this.output = new ItemStackHandler(1) {
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                Item item = stack.getItem();
                if (item == Items.BUCKET || item instanceof ItemBucket || item instanceof UniversalBucket)
                    return super.insertItem(slot, stack, simulate);
                return stack;
            }
        };
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null))
                || (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null) ? (T)this :
                capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (facing == EnumFacing.DOWN ? (T)output : (T)input) : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        writeInternalTank(compound);
        writeTransferCooldown(compound);
        compound.setInteger("bucketCooldown", bucketCooldown);
        compound.setTag("input", input.serializeNBT());
        compound.setTag("output", output.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        readInternalTank(compound);
        readTransferCooldown(compound);
        bucketCooldown = compound.getInteger("bucketCooldown");
        input.deserializeNBT(compound.getCompoundTag("input"));
        output.deserializeNBT(compound.getCompoundTag("output"));
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
    
    public IItemHandler getBucketInput()
    {
        return input;
    }
    
    public IItemHandler getBucketOutput()
    {
        return output;
    }
    
    public int getBucketCooldown()
    {
        return bucketCooldown;
    }
    
    public void setBucketCooldown(int cooldown)
    {
        bucketCooldown = cooldown;
        if (!world.isRemote)
            SimpleFactory.NETWORK.sendToAll(new BucketCooldownMessage(pos, bucketCooldown));
    }
    
    public boolean isBucketCooldown()
    {
        return getBucketCooldown() > 0;
    }
    
    public void resetBucketCooldown()
    {
        setBucketCooldown(25);
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
        return tank.getFluidAmount() > 0;
    }
    
    @Override
    public boolean canTransferIn()
    {
        return tank.getFluidAmount() < tank.getCapacity();
    }
    
    @Override
    public boolean transferOut()
    {
        if (input.getStackInSlot(0).isEmpty() && getBucketCooldown() != 25)
            resetBucketCooldown();
        return doBucketFill() || doTransferOut();
    }
    
    @Override
    public boolean transferIn()
    {
        if (input.getStackInSlot(0).isEmpty() && getBucketCooldown() != 25)
            resetBucketCooldown();
        boolean flag = doBucketDrain();
        flag = doPullFluidBlock() || flag;
        flag = doPullFluid() || flag;
        return flag;
    }
    
    public boolean doTransferOut()
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
    
    public boolean doBucketFill()
    {
        ItemStack inStack = input.getStackInSlot(0);
        ItemStack outStack = output.getStackInSlot(0);
        if (inStack.getItem() == Items.BUCKET)
        {
            if (outStack.isEmpty() && tank.getFluidAmount() >= Fluid.BUCKET_VOLUME)
            {
                setBucketCooldown(getBucketCooldown() - 1);
                if (!isBucketCooldown())
                {
                    resetBucketCooldown();
                    input.extractItem(0, 1, false);
                    output.insertItem(0, UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, tank.getFluid().getFluid()), false);
                    drain(Fluid.BUCKET_VOLUME, true);
                }
                return true;
            }
            else
            {
                resetBucketCooldown();
            }
            return true;
        }
        return false;
    }
    
    public boolean doPullFluidBlock()
    {
        BlockPos above = pos.up();
        Block aboveBlock = world.getBlockState(above).getBlock();
        Fluid fromFluid = FluidRegistry.lookupFluidForBlock(aboveBlock);
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
            if ((aboveBlock.getMetaFromState(world.getBlockState(above)) == 0
                    && (tank.getFluid() == null || tank.getFluid().getFluid() == fromFluid))
                    || (aboveBlock instanceof BlockFluidBase && aboveBlock.getMetaFromState(world.getBlockState(above)) == 15))
            {
                world.setBlockToAir(above);
                fill(new FluidStack(fromFluid, Fluid.BUCKET_VOLUME), true);
                return true;
            }
        }
        return false;
    }
    
    public boolean doPullFluid()
    {
        TileEntity tileAbove = world.getTileEntity(pos.up());
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
        return false;
    }
    
    public boolean doBucketDrain()
    {
        ItemStack inStack = input.getStackInSlot(0);
        ItemStack outStack = output.getStackInSlot(0);
        if (inStack.getItem() != Items.BUCKET && (inStack.getItem() instanceof ItemBucket || inStack.getItem() instanceof UniversalBucket))
        {
            if ((outStack.isEmpty() || (outStack.getItem() == Items.BUCKET && outStack.getCount() < outStack.getMaxStackSize()))
                    && tank.getFluidAmount() <= tank.getCapacity() - Fluid.BUCKET_VOLUME)
            {
                setBucketCooldown(getBucketCooldown() - 1);
                if (!isBucketCooldown())
                {
                    resetBucketCooldown();
                    fill(new FluidStack(FluidUtil.getFluidContained(inStack), Fluid.BUCKET_VOLUME), true);
                    input.extractItem(0, 1, false);
                    output.insertItem(0, new ItemStack(Items.BUCKET), false);
                }
            }
            else
            {
                resetBucketCooldown();
            }
            return true;
        }
        return false;
    }
}
