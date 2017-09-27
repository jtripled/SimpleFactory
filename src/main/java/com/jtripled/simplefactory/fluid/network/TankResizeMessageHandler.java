package com.jtripled.simplefactory.fluid.network;

import com.jtripled.simplefactory.fluid.TileTank;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 *
 * @author jtripled
 */
public class TankResizeMessageHandler implements IMessageHandler<TankResizeMessage, IMessage>
{
    @Override
    public IMessage onMessage(TankResizeMessage message, MessageContext context)
    {
        World world = Minecraft.getMinecraft().player.world;
        TileEntity tile = world.getTileEntity(message.getPos());
        if (tile instanceof TileTank)
        {
            TileTank tank = (TileTank) tile;
            tank.getInternalTank().setCapacity(message.getCapacity());
        }
        return null;
    }
}
