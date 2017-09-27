package com.jtripled.simplefactory;

import com.jtripled.simplefactory.blocks.FluidDuctBlock;
import com.jtripled.simplefactory.blocks.PumpBlock;
import com.jtripled.simplefactory.blocks.GratedHopperBlock;
import com.jtripled.simplefactory.blocks.ItemDuctBlock;
import com.jtripled.simplefactory.blocks.TankBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 *
 * @author jtripled
 */
@Mod.EventBusSubscriber
public class SimpleFactoryRegistry implements IGuiHandler
{
    /**
     * Factory.
     */
    
    public static final BlockBase FLUID_DUCT = new FluidDuctBlock();
    public static final BlockBase GRATED_HOPPER = new GratedHopperBlock();
    public static final BlockBase ITEM_DUCT = new ItemDuctBlock();
    public static final BlockBase PUMP = new PumpBlock();
    public static final BlockBase TANK = new TankBlock();
    
    /**
     * Add blocks to registry list.
     */
    
    public void addBlocks()
    {
        // Factory
        addBlock(FLUID_DUCT);
        addBlock(GRATED_HOPPER);
        addBlock(ITEM_DUCT);
        addBlock(PUMP);
        addBlock(TANK);
    }
    
    /**
     * Internal registry handling.
     */
    
    private final List<BlockBase> BLOCKS = new ArrayList<>();
    private IForgeRegistry<Block> blockRegistry;
    private IForgeRegistry<Item> itemRegistry;
    
    private void addBlock(BlockBase block)
    {
        BLOCKS.add(block);
    }
    
    protected void onRegisterBlocks(IForgeRegistry<Block> registry)
    {
        blockRegistry = registry;
        addBlocks();
        for (BlockBase block : BLOCKS)
            block.registerBlock(this);
        blockRegistry = null;
    }
    
    protected void onRegisterItems(IForgeRegistry<Item> registry)
    {
        itemRegistry = registry;
        for (BlockBase block : BLOCKS)
            block.registerItem(this);
        itemRegistry = null;
    }
    
    protected void onRegisterRenderers()
    {
        for (BlockBase block : BLOCKS)
            block.registerRenderer(this);
    }
    
    /*
     * Block registration hooks.
     */
    
    public void registerBlock(Block block)
    {
        blockRegistry.register(block);
    }
    
    public void registerTileEntity(Block block, Class<? extends TileEntity> tileClass)
    {
        GameRegistry.registerTileEntity(tileClass, block.getRegistryName().toString());
    }
    
    /*
     * Item registration hooks.
     */
    
    public void registerItem(Item item)
    {
        itemRegistry.register(item);
    }
    
    /*
     * Renderer registration hooks.
     */
    
    public void registerItemRenderer(Item item, String name)
    {
        SimpleFactory.PROXY.registerItemRenderer(item, name);
    }
    
    /*
     * GUI registration hooks.
     */
    
    private static final Map<Integer, GUIBase> GUIS = new HashMap<>();
    private static int GUI_ID = 0;
    
    public static int nextGUIID()
    {
        int next = GUI_ID;
        GUI_ID = GUI_ID + 1;
        return next;
    }
    
    public void registerGUI(GUIBase element)
    {
        GUIBase holder = (GUIBase) element;
        GUIS.put(holder.getGUIID(), holder);
    }
    
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        return GUIS.containsKey(id)
                ? GUIS.get(id).getServerGUI(player, world, x, y, z)
                : null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        return GUIS.containsKey(id)
                ? GUIS.get(id).getClientGUI(player, world, x, y, z)
                : null;
    }
    
    /*
     * Event handlers to allow for registration.
     */
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        SimpleFactory.REGISTRY.onRegisterItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        SimpleFactory.REGISTRY.onRegisterBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerRenderers(ModelRegistryEvent event)
    {
        SimpleFactory.REGISTRY.onRegisterRenderers();
    }
    
    public void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(SimpleFactory.INSTANCE, this);
    }
    
    public void init(FMLInitializationEvent event)
    {
        
    }
    
    public void postInit(FMLPostInitializationEvent event)
    {
        
    }
    
    /*
     * Interfaces and classes to implement on registrable objects.
     */
    
    public static interface BlockBase
    {
        public void registerBlock(SimpleFactoryRegistry registry);

        public void registerItem(SimpleFactoryRegistry registry);

        public void registerRenderer(SimpleFactoryRegistry registry);
    }
    
    public static interface ItemBase
    {
        public void registerItem(SimpleFactoryRegistry registry);

        public void registerRenderer(SimpleFactoryRegistry registry);
    }
    
    public static interface GUIBase
    {
        public int getGUIID();
        
        public Object getServerGUI(EntityPlayer player, World world, int x, int y, int z);

        public Object getClientGUI(EntityPlayer player, World world, int x, int y, int z);
        
        public void openGUI(EntityPlayer player, World world, int x, int y, int z);
    }
}
