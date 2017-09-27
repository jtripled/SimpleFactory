package com.jtripled.simplefactory.blocks;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.SimpleFactoryRegistry;
import com.jtripled.simplefactory.SimpleFactoryRegistry.*;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import static net.minecraft.block.Block.FULL_BLOCK_AABB;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author jtripled
 */
public class PumpBlock extends Block implements BlockBase, GUIBase
{
    public static final String NAME = "pump";
    public static final int GUI_ID = SimpleFactoryRegistry.nextGUIID();
    public static final PropertyDirection FACING = PropertyDirection.create("facing", (@Nullable EnumFacing face) -> face != EnumFacing.UP);
    public static final PropertyBool ENABLED = PropertyBool.create("enabled");
    
    protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    
    private final ItemBlock item;
    
    public PumpBlock()
    {
        super(Material.IRON);
        this.setUnlocalizedName(NAME);
        this.setRegistryName(new ResourceLocation(SimpleFactory.ID, NAME));
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(ENABLED, true));
        this.item = new ItemBlock(this);
        this.item.setUnlocalizedName(this.getUnlocalizedName());
        this.item.setRegistryName(this.getRegistryName());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
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
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
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

    public static EnumFacing getFacing(int meta)
    {
        return EnumFacing.getFront(meta & 7);
    }

    public static boolean isEnabled(int meta)
    {
        return (meta & 8) != 8;
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            PumpTile tile = (PumpTile) world.getTileEntity(pos);
            TextComponentString message;
            if (tile.isEmpty())
                message = new TextComponentString("This fluid tank is empty.");
            else
                message = new TextComponentString("This fluid tank contains " + tile.tank.getFluidAmount() + "mB of " + tile.tank.getFluid().getFluid().getName() + ".");
            message.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(message);
            openGUI(player, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(ENABLED, isEnabled(meta));
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
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new PumpTile();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, ENABLED});
    }

    @Override
    public int getGUIID()
    {
        return GUI_ID;
    }

    @Override
    public PumpContainer getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new PumpContainer((PumpTile) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
    }

    @Override
    public PumpGUI getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new PumpGUI(getServerGUI(player, world, x, y, z));
    }
    
    @Override
    public void openGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        player.openGui(SimpleFactory.INSTANCE, GUI_ID, world, x, y, z);
    }
    
    @Override
    public void registerBlock(SimpleFactoryRegistry registry)
    {
        SimpleFactory.PROXY.registerBlockStateMap(this, (new StateMap.Builder()).ignore(ENABLED).build());
        registry.registerBlock(this);
        registry.registerTileEntity(this, PumpTile.class);
        registry.registerGUI(this);
    }
    
    @Override
    public void registerItem(SimpleFactoryRegistry registry)
    {
        registry.registerItem(item);
    }
    
    @Override
    public void registerRenderer(SimpleFactoryRegistry registry)
    {
        registry.registerItemRenderer(item, NAME);
    }
}
