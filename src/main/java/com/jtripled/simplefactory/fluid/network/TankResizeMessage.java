package com.jtripled.simplefactory.fluid.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 *
 * @author jtripled
 */
public class TankResizeMessage implements IMessage
{
    private BlockPos pos;
    private int capacity;

    public TankResizeMessage()
    {

    }

    public TankResizeMessage(BlockPos pos, int capacity)
    {
        this.pos = pos;
        this.capacity = capacity;
    }

    public BlockPos getPos()
    {
        return pos;
    }
    
    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        capacity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(capacity);
    }
}
