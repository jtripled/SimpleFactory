package com.jtripled.voxen.block;

import com.jtripled.voxen.item.ItemBase;
import com.jtripled.voxen.item.ItemBlockBase;
import com.jtripled.voxen.mod.VoxenMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

/**
 *
 * @author jtripled
 */
public class BlockBase extends Block implements IBlockBase
{
    private final String name;
    private ItemBlockBase item;

    public BlockBase(Material material, String name)
    {
        this(material, material.getMaterialMapColor(), name);
    }

    public BlockBase(Material material, MapColor mapColor, String name)
    {
        super(material, mapColor);
        this.name = name;
        this.setUnlocalizedName(name);
        this.setRegistryName(new ResourceLocation(VoxenMod.ID, name));
    }

    @Override
    public String getName()
    {
        return name;
    }
    
    public void setItem()
    {
        this.item = new ItemBlockBase(this);
        this.item.setUnlocalizedName(this.getUnlocalizedName());
        this.item.setRegistryName(this.getRegistryName());
    }
    
    @Override
    public ItemBase getItem()
    {
        return item;
    }
}
