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
public abstract class RegistrationHandler
{
    public void onRegisterBlocks(VoxenRegistry registry) {}
    
    public void onRegisterItems(VoxenRegistry registry) {}
    
    public void onRegisterEntities(VoxenRegistry registry) {}
    
    public void onRegisterMessages(VoxenRegistry registry) {}
    
    public final void registerBlock(BlockBase block)
    {
        VoxenMod.REGISTRY.addBlock(block);
    }
    
    public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handler, Class<REQ> message, Side side)
    {
        VoxenMod.REGISTRY.registerMessage(handler, message, side);
    }
}
