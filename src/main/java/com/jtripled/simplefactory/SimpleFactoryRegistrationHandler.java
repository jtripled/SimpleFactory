package com.jtripled.simplefactory;

import com.jtripled.simplefactory.fluid.block.BlockFluidDuct;
import com.jtripled.simplefactory.fluid.block.BlockPump;
import com.jtripled.simplefactory.fluid.block.BlockTank;
import com.jtripled.simplefactory.fluid.network.FluidMessage;
import com.jtripled.simplefactory.fluid.network.FluidMessageHandler;
import com.jtripled.simplefactory.fluid.network.TankResizeMessage;
import com.jtripled.simplefactory.fluid.network.TankResizeMessageHandler;
import com.jtripled.voxen.block.BlockBase;
import com.jtripled.voxen.registry.RegistrationHandler;
import com.jtripled.voxen.registry.VoxenRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 *
 * @author jtripled
 */
public class SimpleFactoryRegistrationHandler extends RegistrationHandler
{
    public static final BlockBase FLUID_DUCT = new BlockFluidDuct();
    public static final BlockBase PUMP = new BlockPump();
    public static final BlockBase TANK = new BlockTank();
    
    @Override
    public void onRegisterBlocks(VoxenRegistry registry)
    {
        registerBlock(FLUID_DUCT);
        registerBlock(PUMP);
        registerBlock(TANK);
    }
    
    @Override
    public void onRegisterMessages(VoxenRegistry registry)
    {
        registerMessage(FluidMessageHandler.class, FluidMessage.class, Side.CLIENT);
        registerMessage(TankResizeMessageHandler.class, TankResizeMessage.class, Side.CLIENT);
    }
}
