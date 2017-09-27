package com.jtripled.simplefactory.fluid.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import scala.Int;

/**
 *
 * @author jtripled
 */
public class FluidMessageHandler implements IMessageHandler<FluidMessage, IMessage>
{
    @Override
    public IMessage onMessage(FluidMessage message, MessageContext context)
    {
        World world = Minecraft.getMinecraft().player.world;
        TileEntity tile = world.getTileEntity(message.getPos());
        if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            handler.drain(Int.MaxValue(), true);
            if (message.getFluidStack() != null)
            {
                handler.fill(message.getFluidStack(), true);
            }
        }
        return null;
    }
}
