package com.jtripled.voxen.proxy;

import com.jtripled.voxen.block.BlockBase;
import com.jtripled.voxen.item.ItemBase;
import com.jtripled.voxen.mod.VoxenMod;
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
public class VoxenClientProxy extends VoxenCommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }

    @Override
    public void registerItemRenderer(ItemBase item, String id)
    {
        registerItemRenderer(item, 0, new ModelResourceLocation(VoxenMod.ID + ":" + id, "inventory"));
    }

    @Override
    public void registerItemRenderer(ItemBase item, int meta, String id)
    {
        registerItemRenderer(item, meta, new ModelResourceLocation(VoxenMod.ID + ":" + id, "inventory"));
    }

    @Override
    public void registerItemRenderer(ItemBase item, int meta, ModelResourceLocation resource)
    {
        ModelLoader.setCustomModelResourceLocation((Item) item, meta, resource);
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
