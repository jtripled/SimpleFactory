package com.jtripled.simplefactory;

import com.jtripled.simplefactory.SimpleFactoryRegistry.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 *
 * @author jtripled
 */
public class SimpleFactoryClientProxy extends SimpleFactoryCommonProxy
{
    @Override
    protected void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Override
    protected void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @Override
    protected void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }

    @Override
    public void registerItemRenderer(Item item, String id)
    {
        registerItemRenderer(item, 0, new ModelResourceLocation(SimpleFactory.ID + ":" + id, "normal"));
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id)
    {
        registerItemRenderer(item, meta, new ModelResourceLocation(SimpleFactory.ID + ":" + id, "normal"));
    }

    @Override
    public void registerItemRenderer(Item item, int meta, ModelResourceLocation resource)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, resource);
    }

    @Override
    public void registerTileRenderer(Class tileClass, TileEntitySpecialRenderer renderer)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(tileClass, renderer);
    }

    @Override
    public void registerEntityRenderer(Class<? extends Entity> entityClass, IRenderFactory renderFactory)
    {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
    }

    @Override
    public void registerBlockStateMap(BlockBase block, IStateMapper map)
    {
        ModelLoader.setCustomStateMapper((Block) block, map);
    }

    @Override
    public String localize(String unlocalized, Object... args)
    {
        return net.minecraft.client.resources.I18n.format(unlocalized, args);
    }
}
