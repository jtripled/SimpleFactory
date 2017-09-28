package com.jtripled.simplefactory.blocks;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.voxen.block.BlockBase;
import com.jtripled.voxen.gui.GUIBase;
import com.jtripled.voxen.registry.RegistrationHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

/**
 *
 * @author jtripled
 */
public class ItemDuctBlock extends Block implements BlockBase, GUIBase
{
    public static final String NAME = "item_duct";
    public static final int GUI_ID = RegistrationHandler.nextGUIID();
    public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.<EnumFacing>create("facing", EnumFacing.class);
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    
    private final ItemBlock item;
    
    public ItemDuctBlock()
    {
        super(Material.IRON);
        this.setUnlocalizedName(NAME);
        this.setRegistryName(new ResourceLocation(SimpleFactory.ID, NAME));
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(UP, false).withProperty(DOWN, false));
        this.item = new ItemBlock(this);
        this.item.setUnlocalizedName(this.getUnlocalizedName());
        this.item.setRegistryName(this.getRegistryName());
    }
    
    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN});
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
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        AxisAlignedBB bb = BOUNDING_BOX;
        state = getActualState(state, world, pos);
        boolean north = state.getValue(NORTH);
        boolean east = state.getValue(EAST);
        boolean south = state.getValue(SOUTH);
        boolean west = state.getValue(WEST);
        boolean up = state.getValue(UP);
        boolean down = state.getValue(DOWN);
        switch(state.getValue(FACING))
        {
            case NORTH: north = true; break;
            case EAST: east = true; break;
            case SOUTH: south = true; break;
            case WEST: west = true; break;
            case UP: up = true; break;
            case DOWN: down = true; break;
        }
        if (north)
            bb = bb.expand(0, 0, -0.3125);
        if (east)
            bb = bb.expand(0.3125, 0, 0);
        if (south)
            bb = bb.expand(0, 0, 0.3125);
        if (west)
            bb = bb.expand(-0.3125, 0, 0);
        if (up)
            bb = bb.expand(0, 0.3125, 0);
        if (down)
            bb = bb.expand(0, -0.3125, 0);
        return bb;
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState state = this.getDefaultState().withProperty(FACING, facing.getOpposite());
        return state;
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumFacing facing = ((EnumFacing)state.getValue(FACING));
        return state.withProperty(NORTH, canConnect(state, world, pos.north()) && facing != EnumFacing.NORTH)
             .withProperty(EAST, canConnect(state, world, pos.east()) && facing != EnumFacing.EAST)
             .withProperty(SOUTH, canConnect(state, world, pos.south()) && facing != EnumFacing.SOUTH)
             .withProperty(WEST, canConnect(state, world, pos.west()) && facing != EnumFacing.WEST)
             .withProperty(UP, canConnect(state, world, pos.up()) && facing != EnumFacing.UP)
             .withProperty(DOWN, canConnect(state, world, pos.down()) && facing != EnumFacing.DOWN);
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof ItemDuctTile)
        {
            //InventoryHelper.dropInventoryItems(world, pos, (ItemDuctTile) tile);
            world.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(world, pos, state);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new ItemDuctTile();
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
    
    public boolean canConnect(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }
    
    @Override
    public int getGUIID()
    {
        return GUI_ID;
    }

    @Override
    public ItemDuctContainer getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new ItemDuctContainer((ItemDuctTile) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
    }

    @Override
    public ItemDuctGUI getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new ItemDuctGUI(getServerGUI(player, world, x, y, z));
    }
    
    @Override
    public void openGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        player.openGui(SimpleFactory.INSTANCE, GUI_ID, world, x, y, z);
    }
    
    @Override
    public void registerBlock(RegistrationHandler registry)
    {
        registry.registerBlock(this);
        registry.registerTileEntity(this, ItemDuctTile.class);
        registry.registerGUI(this);
    }
    
    @Override
    public void registerItem(RegistrationHandler registry)
    {
        registry.registerItem(item);
    }
    
    @Override
    public void registerRenderer(RegistrationHandler registry)
    {
        //registry.registerItemRenderer(item, NAME);
    }
}
