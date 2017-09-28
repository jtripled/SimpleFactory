package com.jtripled.simplefactory.fluid.block;

import com.jtripled.simplefactory.fluid.inventory.GUIFluid;
import com.jtripled.simplefactory.fluid.inventory.ContainerFluid;
import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.fluid.tile.TileFluid;
import com.jtripled.voxen.block.BlockBase;
import com.jtripled.voxen.gui.GUIBase;
import com.jtripled.voxen.item.ItemBase;
import com.jtripled.voxen.item.ItemBlockBase;
import com.jtripled.voxen.registry.VoxenRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

/**
 *
 * @author jtripled
 */
public abstract class BlockFluid extends Block implements BlockBase, GUIBase
{
    private final int guiID;
    private final ItemBlockBase item;
    private final String name;
    
    public BlockFluid(Material material, String name)
    {
        super(material);
        this.name = name;
        this.guiID = VoxenRegistry.nextGUIID();
        this.setUnlocalizedName(name);
        this.setRegistryName(new ResourceLocation(SimpleFactory.ID, name));
        this.item = new ItemBlockBase(this);
        this.item.setUnlocalizedName(this.getUnlocalizedName());
        this.item.setRegistryName(this.getRegistryName());
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public int getGUIID()
    {
        return guiID;
    }

    @Override
    public ContainerFluid getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new ContainerFluid((TileFluid) world.getTileEntity(new BlockPos(x, y, z)), player.inventory);
    }

    @Override
    public GUIFluid getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return new GUIFluid(getServerGUI(player, world, x, y, z));
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
            TileFluid tile = (TileFluid) world.getTileEntity(pos);
            FluidTank internalTank = tile.getInternalTank();
            BlockPos internalPos = tile.getInternalTankPos();
            TextComponentString message;
            if (internalTank.getFluidAmount() <= 0)
                message = new TextComponentString("This fluid tank is empty.");
            else
                message = new TextComponentString("This fluid tank contains " + internalTank.getFluidAmount() + "/" + internalTank.getCapacity() + "mB of " + internalTank.getFluid().getFluid().getName() + ".");
            message.getStyle().setColor(TextFormatting.RED);
            player.sendMessage(message);
            openGUI(player, world, internalPos.getX(), internalPos.getY(), internalPos.getZ());
        }
        return true;
    }
    
    @Override
    public abstract Class<? extends TileEntity> getTileClass();
    
    @Override
    public TileFluid createTileEntity(World world, IBlockState state)
    {
        try
        {
            return (TileFluid) getTileClass().newInstance();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
    
    @Override
    public ItemBase getItem()
    {
        return item;
    }
}
