package com.jtripled.simplefactory.item.block;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.item.inventory.ContainerItemDuct;
import com.jtripled.simplefactory.item.inventory.GUIItemDuct;
import com.jtripled.simplefactory.item.tile.TileItem;
import com.jtripled.simplefactory.item.tile.TileItemDuct;
import com.jtripled.voxen.block.BlockDuct;
import com.jtripled.voxen.gui.GUIBase;
import com.jtripled.voxen.registry.RegistrationHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 *
 * @author jtripled
 */
public class BlockItemDuct extends BlockDuct implements GUIBase
{
    private final int guiID;
    
    public BlockItemDuct()
    {
        super(Material.IRON, "item_duct");
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.guiID = RegistrationHandler.nextGUIID();
        this.setItem();
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
    public Object getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new ContainerItemDuct((TileItemDuct) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
    }

    @Override
    public Object getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new GUIItemDuct((ContainerItemDuct) getServerGUI(player, world, x, y, z));
    }
    
    @Override
    public Class<? extends TileEntity> getTileClass()
    {
        return TileItemDuct.class;
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
    
    @Override
    public TileItemDuct createTileEntity(World world, IBlockState state)
    {
        return new TileItemDuct();
    }
    
    public static EnumFacing getFacing(IBlockState state)
    {
        return state.getValue(FACING);
    }

    @Override
    public boolean canConnect(IBlockState state, IBlockAccess world, BlockPos otherPos, EnumFacing otherFacing)
    {
        TileEntity tile = world.getTileEntity(otherPos);
        return tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, otherFacing);
    }
}
