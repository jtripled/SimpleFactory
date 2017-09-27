package com.jtripled.simplefactory;

import com.jtripled.simplefactory.SimpleFactoryRegistry.BlockBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 *
 * @author jtripled
 */
public class SimpleFactoryCommonProxy
{
    protected void preInit(FMLPreInitializationEvent event)
    {
        
    }
    
    protected void init(FMLInitializationEvent event)
    {
        
    }
    
    protected void postInit(FMLPostInitializationEvent event)
    {
        
    }

    public void registerItemRenderer(Item item, String id)
    {
        
    }

    public void registerItemRenderer(Item item, int meta, String id)
    {
        
    }

    public void registerItemRenderer(Item item, int meta, ModelResourceLocation resource)
    {
        
    }
    
    public void registerTileRenderer(Class tileClass, TileEntitySpecialRenderer renderer)
    {
        
    }
    
    public void registerEntityRenderer(Class<? extends Entity> entityClass, IRenderFactory renderFactory)
    {
        
    }
    
    public void registerBlockStateMap(BlockBase block, IStateMapper map)
    {
        
    }
    
    public String localize(String unlocalized, Object... args)
    {
        return I18n.translateToLocalFormatted(unlocalized, args);
    }
}
