package com.jtripled.simplefactory;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(name = SimpleFactory.NAME, modid = SimpleFactory.ID, version = SimpleFactory.VERSION)
public class SimpleFactory
{
    public static final String NAME = "SimpleFactory";
    public static final String ID = "simplefactory";
    public static final String VERSION = "1.12.2";
    public static final String COMMON_PROXY = "com.simplefactory.SimpleFactoryCommonProxy";
    public static final String CLIENT_PROXY = "com.simplefactory.SimpleFactoryClientProxy";
    
    @Mod.Instance(SimpleFactory.ID)
    public static SimpleFactory INSTANCE;
    
    @SidedProxy(serverSide = SimpleFactory.COMMON_PROXY, clientSide = SimpleFactory.CLIENT_PROXY)
    public static SimpleFactoryCommonProxy PROXY;
    public static final SimpleFactoryRegistry REGISTRY = new SimpleFactoryRegistry();
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ID);
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PROXY.preInit(event);
        REGISTRY.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init(event);
        REGISTRY.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        PROXY.postInit(event);
        REGISTRY.postInit(event);
    }
}
