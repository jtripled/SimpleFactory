package com.jtripled.simplefactory.fluid.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
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
        int amount = buf.readInt();
        if (amount > 0)
        {
            String name = ByteBufUtils.readUTF8String(buf);
            fluid = new FluidStack(FluidRegistry.getFluid(name), amount);
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
            buf.writeInt(0);
            ByteBufUtils.writeUTF8String(buf, "null");
        }
        else
        {
            buf.writeInt(fluid.amount);
            ByteBufUtils.writeUTF8String(buf, fluid.getFluid().getName());
        }
    }
}
