package com.jtripled.simplefactory.fluid.network;

import com.jtripled.simplefactory.fluid.tile.TilePump;
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
public class BucketCooldownMessageHandler implements IMessageHandler<BucketCooldownMessage, IMessage>
{
    @Override
    public IMessage onMessage(BucketCooldownMessage message, MessageContext context)
    {
        World world = Minecraft.getMinecraft().player.world;
        TileEntity tile = world.getTileEntity(message.getPos());
        if (tile instanceof TilePump)
        {
            TilePump pump = (TilePump) tile;
            pump.setBucketCooldown(message.getCooldown());
        }
        return null;
    }
}
