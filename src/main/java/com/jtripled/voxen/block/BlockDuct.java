package com.jtripled.voxen.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 *
 * @author jtripled
 */
public abstract class BlockDuct extends BlockBase implements IBlockDuct
{
    public BlockDuct(Material material, String name)
    {
        this(material, material.getMaterialMapColor(), name);
    }
    
    public BlockDuct(Material material, MapColor mapColor, String name)
    {
        super(material, mapColor, name);
        this.setDefaultState(getDefaultDuctState());
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return createBlockStateContainer();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return createStateFromMeta(meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return createMetaFromState(state);
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return createBoundingBox(state, world, pos);
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState state = getDefaultState();
        if (hasFacing())
            state = state.withProperty(FACING, facing.getOpposite());
        return state;
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return createActualState(state, world, pos);
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
}
