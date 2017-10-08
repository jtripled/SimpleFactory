package com.jtripled.simplefactory.fluid.block;

import com.jtripled.simplefactory.fluid.inventory.ContainerTank;
import com.jtripled.simplefactory.fluid.inventory.GUITank;
import com.jtripled.simplefactory.fluid.tile.TileFluidDuct;
import com.jtripled.voxen.block.BlockDuct;
import com.jtripled.voxen.gui.GUIBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

/**
 *
 * @author jtripled
 */
public class BlockFluidDuct extends BlockDuct implements GUIBase
{
    public BlockFluidDuct()
    {
        super(Material.IRON, "fluid_duct");
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setItem();
    }

    @Override
    public Object getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new ContainerTank((TileFluidDuct) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
    }

    @Override
    public Object getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new GUITank((ContainerTank) getServerGUI(player, world, x, y, z));
    }
    
    @Override
    public Class<? extends TileEntity> getTileClass()
    {
        return TileFluidDuct.class;
    }

    @Override
    public boolean canConnect(IBlockState state, IBlockAccess world, BlockPos otherPos, EnumFacing otherFacing)
    {
        TileEntity tile = world.getTileEntity(otherPos);
        return tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, otherFacing);
    }
}
