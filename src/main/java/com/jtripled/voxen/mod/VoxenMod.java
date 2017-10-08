package com.jtripled.voxen.mod;

import com.jtripled.voxen.proxy.VoxenCommonProxy;
import com.jtripled.voxen.registry.Registry;
import com.jtripled.voxen.registry.RegistrationHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
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
    
    public static final Registry REGISTRY = VoxenConfig.REGISTRY;
    
    @Mod.Instance(VoxenMod.ID)
    public static VoxenMod INSTANCE;
    
    @SidedProxy(serverSide = VoxenMod.COMMON_PROXY, clientSide = VoxenMod.CLIENT_PROXY)
    public static VoxenCommonProxy PROXY;
    
    public static RegistrationHandler REGISTRATION_HANDLER = new RegistrationHandler();
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(VoxenMod.ID);
    
    static
    {
        FluidRegistry.enableUniversalBucket();
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PROXY.preInit(event);
        REGISTRATION_HANDLER.onPreInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init(event);
        REGISTRATION_HANDLER.onInit(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, REGISTRATION_HANDLER);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        PROXY.postInit(event);
        REGISTRATION_HANDLER.onPostInit(event);
    }
    
    @Mod.EventBusSubscriber
    public static class RegistrationEventHandler
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            REGISTRATION_HANDLER.onRegisterItems(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            REGISTRATION_HANDLER.onRegisterBlocks(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
        {
            REGISTRATION_HANDLER.onRegisterEntities(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerRenderers(ModelRegistryEvent event)
        {
            REGISTRATION_HANDLER.onRegisterRenderers();
        }
    }
}
