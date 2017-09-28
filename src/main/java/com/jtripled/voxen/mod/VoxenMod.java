package com.jtripled.voxen.mod;

import com.jtripled.voxen.proxy.VoxenCommonProxy;
import com.jtripled.voxen.registry.RegistrationHandler;
import com.jtripled.voxen.registry.VoxenRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;

/**
 *
 * @author jtripled
 */
@Mod(name = VoxenMod.NAME, modid = VoxenMod.ID, version = VoxenMod.VERSION)
public class VoxenMod
{
    public static final String ID = VoxenConfig.ID;
    public static final String NAME = VoxenConfig.NAME;
    public static final String DESCRIPTION = VoxenConfig.DESCRIPTION;
    public static final String VERSION = VoxenConfig.VERSION;
    
    public static final String COMMON_PROXY = "com.jtripled.voxen.proxy.VoxenCommonProxy";
    public static final String CLIENT_PROXY = "com.jtripled.voxen.proxy.VoxenClientProxy";
    
    public static final RegistrationHandler REGISTRATION_HANDLER = VoxenConfig.REGISTRATION_HANDLER;
    
    @Mod.Instance(VoxenMod.ID)
    public static VoxenMod INSTANCE;
    
    @SidedProxy(serverSide = VoxenMod.COMMON_PROXY, clientSide = VoxenMod.CLIENT_PROXY)
    public static VoxenCommonProxy PROXY;
    
    public static VoxenRegistry REGISTRY = new VoxenRegistry();
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(VoxenMod.ID);
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PROXY.preInit(event);
        REGISTRY.onPreInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init(event);
        REGISTRY.onInit(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, REGISTRY);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        PROXY.postInit(event);
        REGISTRY.onPostInit(event);
    }
    
    @Mod.EventBusSubscriber
    public static class RegistrationEventHandler
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            REGISTRY.onRegisterItems(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            REGISTRY.onRegisterBlocks(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
        {
            REGISTRY.onRegisterEntities(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerRenderers(ModelRegistryEvent event)
        {
            REGISTRY.onRegisterRenderers();
        }
    }
}
