package com.jtripled.voxen.gui;

import com.jtripled.voxen.block.IBlockBase;
import com.jtripled.voxen.mod.VoxenMod;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author jtripled
 */
public interface GUIBase
{
    public int getGUIID();
    
    public Class<? extends Container> getContainerClass();
    
    public Class<? extends Gui> getGUIClass();
    
    public default Container getServerGUI(EntityPlayer player, World world, BlockPos pos)
    {
        Class<? extends Container> type = getContainerClass();
        try
        {
            if (this instanceof IBlockBase && ((IBlockBase) this).getTileClass() != null)
                return (Container) type.getConstructor(TileEntity.class, InventoryPlayer.class).newInstance(world.getTileEntity(pos), player.inventory);
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
        {
            Logger.getLogger(GUIBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public default Gui getClientGUI(EntityPlayer player, World world, BlockPos pos)
    {
        Class<? extends Gui> type = getGUIClass();
        try
        {
            return (Gui) type.getConstructor(Container.class).newInstance(getServerGUI(player, world, pos));
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
        {
            Logger.getLogger(GUIBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public default Container getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        
        return getServerGUI(player, world, new BlockPos(x, y, z));
    }

    public default Gui getClientGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return getClientGUI(player, world, new BlockPos(x, y, z));
    }

    public default void openGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        player.openGui(VoxenMod.INSTANCE, getGUIID(), world, x, y, z);
    }

    public default void openGUI(EntityPlayer player, World world, BlockPos pos)
    {
        openGUI(player, world, pos.getX(), pos.getY(), pos.getZ());
    }
}
