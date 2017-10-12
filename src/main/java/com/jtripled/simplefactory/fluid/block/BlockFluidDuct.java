package com.jtripled.simplefactory.fluid.block;

import com.jtripled.simplefactory.fluid.gui.GUIFluidDuct;
import com.jtripled.simplefactory.fluid.container.ContainerFluidDuct;
import com.jtripled.simplefactory.fluid.tile.TileFluidDuct;
import com.jtripled.voxen.block.BlockDuct;
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
import com.jtripled.voxen.gui.GUIHolder;

/**
 *
 * @author jtripled
 */
public class BlockFluidDuct extends BlockDuct implements GUIHolder
{
    public BlockFluidDuct()
    {
        super(Material.IRON, "fluid_duct");
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setItem();
    }
    
    @Override
    public Class<TileFluidDuct> getTileClass()
    {
        return TileFluidDuct.class;
    }
    
    @Override
    public ContainerFluidDuct getServerGUI(EntityPlayer player, World world, BlockPos pos)
    {
        return new ContainerFluidDuct((TileFluidDuct) world.getTileEntity(pos), player.inventory);
    }
    
    @Override
    public GUIFluidDuct getClientGUI(EntityPlayer player, World world, BlockPos pos)
    {
        return new GUIFluidDuct(getServerGUI(player, world, pos));
    }

    @Override
    public boolean canConnect(IBlockState state, IBlockAccess world, BlockPos otherPos, EnumFacing otherFacing)
    {
        TileEntity tile = world.getTileEntity(otherPos);
        return tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, otherFacing);
    }
}
