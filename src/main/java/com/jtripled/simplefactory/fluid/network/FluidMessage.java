package com.jtripled.simplefactory.fluid.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 *
 * @author jtripled
 */
public class FluidMessage implements IMessage
{
    private BlockPos pos;
    private FluidStack fluid;

    public FluidMessage()
    {

    }

    public FluidMessage(BlockPos pos, FluidStack fluid)
    {
        this.pos = pos;
        this.fluid = fluid;
    }

    public BlockPos getPos()
    {
        return pos;
    }
    
    public FluidStack getFluidStack()
    {
        return fluid;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        int id = buf.readInt();
        int amount = buf.readInt();
        if (id != NULL_ID && amount > 0)
        {
            fluid = new FluidStack(getFluidFromID(id), amount);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        if (fluid == null)
        {
            buf.writeInt(NULL_ID);
            buf.writeInt(0);
        }
        else
        {
            buf.writeInt(getIDFromFluid(fluid.getFluid()));
            buf.writeInt(fluid.amount);
        }
    }
    
    public static final int NULL_ID = -1;
    public static final int WATER_ID = 0;
    public static final int LAVA_ID = 1;
    
    public static Fluid getFluidFromID(int id)
    {
        switch (id)
        {
            case NULL_ID: return null;
            case WATER_ID: return FluidRegistry.WATER;
            case LAVA_ID: return FluidRegistry.LAVA;
            default: return null;
        }
    }
    
    public static int getIDFromFluid(Fluid fluid)
    {
        if (fluid == null) return NULL_ID;
        if (fluid == FluidRegistry.WATER) return WATER_ID;
        if (fluid == FluidRegistry.LAVA) return LAVA_ID;
        return NULL_ID;
    }
}
