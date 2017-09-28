package com.jtripled.voxen.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 *
 * @author jtripled
 */
public interface GUIBase
{
    public int getGUIID();

    public Object getServerGUI(EntityPlayer player, World world, int x, int y, int z);

    public Object getClientGUI(EntityPlayer player, World world, int x, int y, int z);

    public void openGUI(EntityPlayer player, World world, int x, int y, int z);
}
