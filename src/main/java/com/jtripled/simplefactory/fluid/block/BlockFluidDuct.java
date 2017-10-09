package com.jtripled.simplefactory.fluid.block;

import com.jtripled.simplefactory.fluid.gui.GUIFluidDuct;
import com.jtripled.simplefactory.fluid.inventory.ContainerFluidDuct;
import com.jtripled.simplefactory.fluid.tile.TileFluidDuct;
import com.jtripled.voxen.block.BlockDuct;
import com.jtripled.voxen.gui.GUIBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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
    public Class<TileFluidDuct> getTileClass()
    {
        return TileFluidDuct.class;
    }
    
    @Override
    public Class<ContainerFluidDuct> getContainerClass()
    {
        return ContainerFluidDuct.class;
    }
    
    @Override
    public Class<GUIFluidDuct> getGUIClass()
    {
        return GUIFluidDuct.class;
    }

    @Override
    public boolean canConnect(IBlockState state, IBlockAccess world, BlockPos otherPos, EnumFacing otherFacing)
    {
        TileEntity tile = world.getTileEntity(otherPos);
        return tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, otherFacing);
    }
}
