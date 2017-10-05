package com.jtripled.simplefactory.fluid.block;

import com.jtripled.simplefactory.fluid.inventory.ContainerFluid;
import com.jtripled.simplefactory.fluid.inventory.GUIFluid;
import com.jtripled.simplefactory.fluid.tile.TilePump;
import com.jtripled.voxen.block.BlockBase;
import com.jtripled.voxen.gui.GUIBase;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author jtripled
 */
public class BlockPump extends BlockBase implements GUIBase
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", (@Nullable EnumFacing face) -> face != EnumFacing.UP);
    public static final PropertyBool ENABLED = PropertyBool.create("enabled");
    
    public BlockPump()
    {
        super(Material.IRON, "pump");
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setItem();
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(ENABLED, true));
    }

    @Override
    public Object getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new ContainerFluid((TilePump) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
    }

    @Override
    public Object getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new GUIFluid((ContainerFluid) getServerGUI(player, world, x, y, z));
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        EnumFacing enumfacing = facing.getOpposite();
        if (enumfacing == EnumFacing.UP)
            enumfacing = EnumFacing.DOWN;
        return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(ENABLED, true);
    }

    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return true;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        this.updateState(world, pos, state);
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        this.updateState(worldIn, pos, state);
    }

    private void updateState(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean flag = !worldIn.isBlockPowered(pos);
        if (flag != ((boolean)state.getValue(ENABLED)))
            worldIn.setBlockState(pos, state.withProperty(ENABLED, flag), 4);
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(ENABLED, (meta & 8) != 8);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();
        if (!((boolean)state.getValue(ENABLED)))
            i |= 8;
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, ENABLED});
    }
    
    @Override
    public Class<? extends TileEntity> getTileClass()
    {
        return TilePump.class;
    }
    
    @Override
    public IProperty[] getIgnoredProperties()
    {
        return new IProperty[] {ENABLED};
    }
}
