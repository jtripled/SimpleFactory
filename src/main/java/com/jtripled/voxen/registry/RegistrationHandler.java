package com.jtripled.voxen.registry;

import com.jtripled.voxen.block.BlockBase;
import com.jtripled.voxen.gui.GUIBase;
import com.jtripled.voxen.item.ItemBase;
import com.jtripled.voxen.mod.VoxenMod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

/**
 *
 * @author jtripled
 */
@Mod.EventBusSubscriber
public class RegistrationHandler implements IGuiHandler
{
    private final List<BlockBase> BLOCKS = new ArrayList<>();
    private IForgeRegistry<Block> blockRegistry;
    private IForgeRegistry<Item> itemRegistry;
    private IForgeRegistry<EntityEntry> entityRegistry;
    
    protected void addBlock(BlockBase block)
    {
        BLOCKS.add(block);
    }
    
    /*
     * Block registration hooks.
     */
    
    public void registerBlock(BlockBase block)
    {
        blockRegistry.register((Block) block);
    }
    
    public void registerTileEntity(BlockBase block, Class<? extends TileEntity> tileClass)
    {
        GameRegistry.registerTileEntity(tileClass, ((Block) block).getRegistryName().toString());
    }
    
    public void registerIgnoredProperties(BlockBase block, IProperty... properties)
    {
        VoxenMod.PROXY.registerBlockStateMap(block, (new StateMap.Builder()).ignore(properties).build());
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
    
    public void registerItemRenderer(ItemBase item, String name)
    {
        VoxenMod.PROXY.registerItemRenderer(item, name);
    }
    
    public void registerTileRenderer(BlockBase block, TileEntitySpecialRenderer tesr)
    {
        VoxenMod.PROXY.registerTileRenderer(block.getTileClass(), tesr);
    }
    
    /*
     * GUI registration hooks.
     */
    
    private static final Map<Integer, GUIBase> GUIS = new HashMap<>();
    private static int GUI_ID = 0;
    private static int MESSAGE_ID = 0;
    
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
    
    public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> handler, Class<REQ> message, Side side)
    {
        VoxenMod.NETWORK.registerMessage(handler, message, MESSAGE_ID++, side);
    }
    
    /*
     * Event handlers to allow for registration.
     */
    
    public void onPreInit(FMLPreInitializationEvent event)
    {
        
    }
    
    public void onInit(FMLInitializationEvent event)
    {
        VoxenMod.REGISTRY.onRegisterMessages(this);
    }
    
    public void onPostInit(FMLPostInitializationEvent event)
    {
        
    }
    
    public void onRegisterBlocks(IForgeRegistry<Block> registry)
    {
        blockRegistry = registry;
        VoxenMod.REGISTRY.onRegisterBlocks(this);
        for (BlockBase block : BLOCKS)
            block.registerBlock(this);
        blockRegistry = null;
    }
    
    public void onRegisterItems(IForgeRegistry<Item> registry)
    {
        itemRegistry = registry;
        for (BlockBase block : BLOCKS)
            block.registerItem(this);
        itemRegistry = null;
    }
    
    public void onRegisterEntities(IForgeRegistry<EntityEntry> registry)
    {
        entityRegistry = registry;
        entityRegistry = null;
    }
    
    public void onRegisterRenderers()
    {
        for (BlockBase block : BLOCKS)
            block.registerRenderer(this);
    }
}
