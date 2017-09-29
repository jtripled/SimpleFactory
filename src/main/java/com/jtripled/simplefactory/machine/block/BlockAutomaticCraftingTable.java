package com.jtripled.simplefactory.machine.block;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.machine.tile.TileAutomaticCraftingTable;
import com.jtripled.voxen.gui.GUIBase;
import com.jtripled.voxen.item.ItemBlockBase;
import com.jtripled.voxen.registry.RegistrationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
public class BlockAutomaticCraftingTable extends Block implements IBlockBase, GUIBase
{
    public static final String NAME = "automatic_crafting_table";
    public static ItemBlockBase ITEM;
    public static int GUIID;

    public BlockAutomaticCraftingTable(Material material)
    {
        super(material);
        this.setUnlocalizedName(NAME);
        this.setRegistryName(new ResourceLocation(SimpleFactory.ID, NAME));
        GUIID = RegistrationHandler.nextGUIID();
        ITEM = new ItemBlockBase(this);
        ITEM.setUnlocalizedName(this.getUnlocalizedName());
        ITEM.setRegistryName(this.getRegistryName());
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public int getGUIID()
    {
        return GUIID;
    }

    @Override
    public Object getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        player.openGui(SimpleFactory.INSTANCE, GUIID, world, x, y, z);
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
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
    
    @Override
    public Class<? extends TileEntity> getTileClass()
    {
        return TileAutomaticCraftingTable.class;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileAutomaticCraftingTable();
    }
    
    @Override
    public ItemBlockBase getItem()
    {
        return ITEM;
    }
}
