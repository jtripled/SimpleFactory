package com.jtripled.simplefactory.fluid.block;

import com.jtripled.simplefactory.fluid.inventory.GUIFluid;
import com.jtripled.simplefactory.fluid.inventory.ContainerFluid;
import com.jtripled.simplefactory.SimpleFactory;
import com.jtripled.simplefactory.SimpleFactoryRegistry;
import com.jtripled.simplefactory.SimpleFactoryRegistry.BlockBase;
import com.jtripled.simplefactory.SimpleFactoryRegistry.GUIBase;
import com.jtripled.simplefactory.fluid.tile.TileFluid;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
    private int guiID;
    
    public BlockFluid(Material material)
    {
        super(material);
        this.guiID = SimpleFactoryRegistry.nextGUIID();
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
    
    @Override
    public abstract TileFluid createTileEntity(World world, IBlockState state);

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
}
