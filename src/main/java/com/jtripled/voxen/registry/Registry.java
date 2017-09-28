package com.jtripled.voxen.registry;

import com.jtripled.voxen.block.BlockBase;
import com.jtripled.voxen.mod.VoxenMod;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 *
 * @author jtripled
 */
public abstract class Registry
{
    public void onRegisterBlocks(RegistrationHandler registry) {}
    
    public void onRegisterItems(RegistrationHandler registry) {}
    
    public void onRegisterEntities(RegistrationHandler registry) {}
    
    public void onRegisterMessages(RegistrationHandler registry) {}
    
    public final void registerBlock(BlockBase block)
    {
        VoxenMod.REGISTRATION_HANDLER.addBlock(block);
    }
    
    public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handler, Class<REQ> message, Side side)
    {
        VoxenMod.REGISTRATION_HANDLER.registerMessage(handler, message, side);
    }
}
