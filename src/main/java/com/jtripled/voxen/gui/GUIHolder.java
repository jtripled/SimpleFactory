package com.jtripled.voxen.gui;

import com.jtripled.voxen.mod.VoxenMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author jtripled
 */
public interface GUIHolder
{
    public int getGUIID();
    
    public default Object getServerGUI(EntityPlayer player, World world, BlockPos pos)
    {
        return null;
    }
    
    public default Object getClientGUI(EntityPlayer player, World world, BlockPos pos)
    {
        return null;
    }

    public default Object getServerGUI(EntityPlayer player, World world, int x, int y, int z)
    {
        return getServerGUI(player, world, new BlockPos(x, y, z));
    }

    public default Object getClientGUI(EntityPlayer player, World world, int x, int y, int z)
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
