package com.jtripled.simplefactory.item.block;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.item.tile.TileItem;
import com.jtripled.voxen.gui.GUIBase;
import com.jtripled.voxen.item.ItemBase;
import com.jtripled.voxen.item.ItemBlockBase;
import com.jtripled.voxen.registry.RegistrationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.jtripled.voxen.block.IBlockBase;

/**
 *
 * @author jtripled
 */
public abstract class BlockItem extends Block implements IBlockBase, GUIBase
{
    private final int guiID;
    private final ItemBlockBase item;
    private final String name;
    
    public BlockItem(Material material, String name)
    {
        super(material);
        this.name = name;
        this.guiID = RegistrationHandler.nextGUIID();
        this.setUnlocalizedName(name);
        this.setRegistryName(new ResourceLocation(SimpleFactory.ID, name));
        this.item = new ItemBlockBase(this);
        this.item.setUnlocalizedName(this.getUnlocalizedName());
        this.item.setRegistryName(this.getRegistryName());
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public int getGUIID()
    {
        return guiID;
    }

    @Override
    public void openGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        player.openGui(SimpleFactory.INSTANCE, guiID, world, x, y, z);
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            openGUI(player, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    @Override
    public abstract Class<? extends TileEntity> getTileClass();
    
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
    
    @Override
    public TileItem createTileEntity(World world, IBlockState state)
    {
        try
        {
            return (TileItem) getTileClass().newInstance();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    
    @Override
    public ItemBase getItem()
    {
        return item;
    }
}
