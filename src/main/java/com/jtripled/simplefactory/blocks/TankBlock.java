package com.jtripled.simplefactory.blocks;

import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.SimpleFactoryRegistry;
import com.jtripled.simplefactory.SimpleFactoryRegistry.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author jtripled
 */
public class TankBlock extends Block implements BlockBase, GUIBase
{
    public static final String NAME = "tank";
    public static final int GUI_ID = SimpleFactoryRegistry.nextGUIID();
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 16);
    
    private final ItemBlock item;
    
    public TankBlock()
    {
        super(Material.IRON);
        this.setUnlocalizedName(NAME);
        this.setRegistryName(new ResourceLocation(SimpleFactory.ID, NAME));
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(UP, false).withProperty(DOWN, false));
        this.item = new ItemBlock(this);
        this.item.setUnlocalizedName(this.getUnlocalizedName());
        this.item.setRegistryName(this.getRegistryName());
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        TileEntity up = worldIn.getTileEntity(pos.up());
        TileEntity down = worldIn.getTileEntity(pos.down());
        return this.getDefaultState().withProperty(UP, up != null && up instanceof TankTile).withProperty(DOWN, down != null && down instanceof TankTile);
    }

    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return true;
    }
    
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        FluidTank lostTank = ((TankTile) world.getTileEntity(pos)).tank;
        super.breakBlock(world, pos, state);
        TileEntity tileUp = world.getTileEntity(pos.up());
        TileEntity tileDown = world.getTileEntity(pos.down());
        boolean up = tileUp != null && tileUp instanceof TankTile;
        boolean down = tileDown != null && tileDown instanceof TankTile;
        if (up && down)
        {
            // Split Tank
            TankTile baseTank = (TankTile) tileUp;
            baseTank.baseTank = baseTank;
            baseTank.tank = new FluidTank(16000);
            BlockPos next = baseTank.getPos().up();
            TileEntity test = world.getTileEntity(next);
            TankTile tank;
            while (test != null && test instanceof TankTile)
            {
                tank = (TankTile) test;
                tank.baseTank = null;
                baseTank.tank.setCapacity(baseTank.tank.getCapacity() + 16000);
                next = next.up();
                test = world.getTileEntity(next);
            }
            baseTank = getBaseTank(world, (TankTile) tileDown);
            baseTank.baseTank = baseTank;
            baseTank.tank.setCapacity(16000);
            next = baseTank.getPos().up();
            test = world.getTileEntity(next);
            while (test != null && test instanceof TankTile)
            {
                tank = (TankTile) test;
                tank.baseTank = null;
                baseTank.tank.setCapacity(baseTank.tank.getCapacity() + 16000);
                next = next.up();
                test = world.getTileEntity(next);
            }
            int toDrain = baseTank.tank.getFluidAmount() - baseTank.tank.getCapacity();
            if (toDrain > 0)
                ((TankTile) tileUp).fill(baseTank.tank.drain(toDrain, true), true);
        }
        else if (up)
        {
            // Shrink Bottom
            TankTile baseTank = (TankTile) tileUp;
            baseTank.baseTank = baseTank;
            baseTank.tank = lostTank;
            baseTank.tank.setCapacity(baseTank.tank.getCapacity() - 16000);
            int toDrain = baseTank.tank.getFluidAmount() - baseTank.tank.getCapacity();
            if (toDrain > 0)
                baseTank.tank.drain(toDrain, true);
            BlockPos next = baseTank.getPos().up();
            TileEntity test = world.getTileEntity(next);
            TankTile tank;
            while (test != null && test instanceof TankTile)
            {
                tank = (TankTile) test;
                tank.baseTank = null;
                next = next.up();
                test = world.getTileEntity(next);
            }
        }
        else if (down)
        {
            // Shrink Top
            TankTile baseTank = getBaseTank(world, (TankTile) tileDown);
            baseTank.tank.setCapacity(baseTank.tank.getCapacity() - 16000);
            int toDrain = baseTank.tank.getFluidAmount() - baseTank.tank.getCapacity();
            if (toDrain > 0)
                baseTank.tank.drain(toDrain, true);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tileUp = world.getTileEntity(pos.up());
        TileEntity tileDown = world.getTileEntity(pos.down());
        boolean up = tileUp != null && tileUp instanceof TankTile;
        boolean down = tileDown != null && tileDown instanceof TankTile;
        if (up && down)
        {
            // Merge Tank
            TankTile mergeTank = (TankTile) world.getTileEntity(pos);
            TankTile baseTank = getBaseTank(world, mergeTank);
            TankTile upTank = (TankTile) tileUp;
            BlockPos next = pos;
            TileEntity test = world.getTileEntity(next);
            TankTile tank;
            FluidStack out = upTank.tank.drain(upTank.tank.getFluidAmount(), true);
            while (test != null && test instanceof TankTile)
            {
                tank = (TankTile) test;
                tank.baseTank = null;
                baseTank.tank.setCapacity(baseTank.tank.getCapacity() + 16000);
                next = next.up();
                test = world.getTileEntity(next);
            }
            if (out != null)
                baseTank.fill(out, true);
        }
        else if (up)
        {
            // Extend Down
            TankTile baseTank = (TankTile) world.getTileEntity(pos);
            TankTile formerBaseTank = (TankTile) tileUp;
            baseTank.tank = formerBaseTank.tank;
            formerBaseTank.tank = new FluidTank(16000);
            baseTank.tank.setCapacity(baseTank.tank.getCapacity() + 16000);
            BlockPos next = formerBaseTank.getPos();
            TileEntity test = world.getTileEntity(next);
            TankTile tank;
            while (test != null && test instanceof TankTile)
            {
                tank = (TankTile) test;
                tank.baseTank = null;
                next = next.up();
                test = world.getTileEntity(next);
            }
        }
        else if (down)
        {
            // Extend Up
            TankTile baseTank = getBaseTank(world, (TankTile) world.getTileEntity(pos));
            baseTank.tank.setCapacity(baseTank.tank.getCapacity() + 16000);
        }
        else
        {
            TankTile tile = (TankTile) world.getTileEntity(pos);
            tile.baseTank = tile;
        }
    }
    
    private static TankTile getBaseTank(World world, TankTile tank)
    {
        BlockPos next = tank.getPos().down();
        TileEntity test = world.getTileEntity(next);
        TankTile tile = tank;
        while (test != null && test instanceof TankTile)
        {
            tile = (TankTile) test;
            next = next.down();
            test = world.getTileEntity(next);
        }
        return tile;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor)
    {
        if (pos.getX() == neighbor.getX() && pos.getZ() == neighbor.getZ())
            updateState(world, pos, world.getBlockState(pos));
    }
    

    private static void updateState(World world, BlockPos pos, IBlockState state)
    {
        BlockPos upPos = pos.up();
        BlockPos downPos = pos.down();
        TileEntity upTest = world.getTileEntity(upPos);
        TileEntity downTest = world.getTileEntity(downPos);
        boolean up = upTest != null && upTest instanceof TankTile;
        boolean down = downTest != null && downTest instanceof TankTile;
        TankTile tile = (TankTile) world.getTileEntity(pos);
        TankTile base = tile.baseTank;
        FluidTank tank = tile.tank;
        world.setBlockState(pos, state.withProperty(UP, up).withProperty(DOWN, down));
        tile = (TankTile) world.getTileEntity(pos);
        tile.tank = tank;
        tile.baseTank = base;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side == EnumFacing.UP)
            return !((boolean) state.getValue(UP));
        else if (side == EnumFacing.DOWN)
            return !((boolean) state.getValue(DOWN));
        return true;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TankTile tile = ((TankTile) world.getTileEntity(pos)).getBaseTank();
            TextComponentString message;
            if (tile.tank.getFluidAmount() <= 0)
                message = new TextComponentString("This fluid tank is empty.");
            else
                message = new TextComponentString("This fluid tank contains " + tile.tank.getFluidAmount() + "/" + tile.tank.getCapacity() + "mB of " + tile.tank.getFluid().getFluid().getName() + ".");
            message.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(message);
            openGUI(player, world, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ());
        }
        return true;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(UP, (meta & 1) == 1)
                .withProperty(DOWN, (meta & 2) == 2);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        if ((boolean) state.getValue(UP)) i |= 1;
        if ((boolean) state.getValue(DOWN)) i |= 2;
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
        return new TankTile();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {DOWN, UP, LEVEL});
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TankTile tank = (TankTile) world.getTileEntity(pos);
        TankTile base = tank.getBaseTank();
        int heightDiff = tank.getPos().getY() - base.getPos().getY();
        int fluidAmount = base.tank.getFluidAmount() - (heightDiff * 16000);
        int level = fluidAmount >= 16000 ? 16 : fluidAmount <= 0 ? 0 :
                (int) Math.ceil(16 * ((float) fluidAmount / 16000));
        return state.withProperty(LEVEL, level);
    }
    
    @Override
    public int getGUIID()
    {
        return GUI_ID;
    }

    @Override
    public TankContainer getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new TankContainer((TankTile) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
    }

    @Override
    public TankGUI getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new TankGUI(getServerGUI(player, world, x, y, z));
    }
    
    @Override
    public void openGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        player.openGui(SimpleFactory.INSTANCE, GUI_ID, world, x, y, z);
    }
    
    @Override
    public void registerBlock(SimpleFactoryRegistry registry)
    {
        SimpleFactory.PROXY.registerBlockStateMap(this, (new StateMap.Builder()).ignore(LEVEL).build());
        registry.registerBlock(this);
        registry.registerTileEntity(this, TankTile.class);
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
        SimpleFactory.PROXY.registerTileRenderer(TankTile.class, new TankTESR());
    }
}
