package com.jtripled.voxen.block;

import com.jtripled.voxen.gui.GUIBase;
import com.jtripled.voxen.item.ItemBase;
import com.jtripled.voxen.item.ItemBlockBase;
import com.jtripled.voxen.mod.VoxenMod;
import com.jtripled.voxen.registry.RegistrationHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author jtripled
 */
public class BlockBase extends Block implements IBlockBase
{
    private final String name;
    private ItemBlockBase item;
    private int guiID;

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
        if (this instanceof GUIBase)
        {
            guiID = RegistrationHandler.nextGUIID();
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            if (this instanceof GUIBase)
                ((GUIBase) this).openGUI(player, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public String getName()
    {
        return name;
    }
    
    public int getGUIID()
    {
        return guiID;
    }
    
    public void setItem()
    {
        this.item = new ItemBlockBase(this);
        this.item.setUnlocalizedName(this.getUnlocalizedName());
        this.item.setRegistryName(this.getRegistryName());
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        System.out.println(getTileClass().toString());
        return getTileClass() != null;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        if (hasTileEntity(state))
        {
            try
            {
                return getTileClass().newInstance();
            }
            catch (InstantiationException | IllegalAccessException ex)
            {
                Logger.getLogger(BlockBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    @Override
    public ItemBase getItem()
    {
        return item;
    }
}
