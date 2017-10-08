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
public class BucketCooldownMessage implements IMessage
{
    private BlockPos pos;
    private int cooldown;

    public BucketCooldownMessage()
    {

    }

    public BucketCooldownMessage(BlockPos pos, int cooldown)
    {
        this.pos = pos;
        this.cooldown = cooldown;
    }

    public BlockPos getPos()
    {
        return pos;
    }
    
    public int getCooldown()
    {
        return cooldown;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        cooldown = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(cooldown);
    }
}
