package com.jtripled.voxen.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author jtripled
 */
public class TileBase extends TileEntity
{
    

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
    }
    
    @Override
    public void onDataPacket(NetworkManager network, SPacketUpdateTileEntity packet)
    {
        readFromNBT(packet.getNbtCompound());
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }
    
    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(super.getUpdateTag());
    }
    
    @Override
    public void handleUpdateTag(NBTTagCompound compound)
    {
        readFromNBT(compound);
    }
}
