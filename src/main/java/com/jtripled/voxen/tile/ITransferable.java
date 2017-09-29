package com.jtripled.voxen.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 *
 * @author jtripled
 */
public interface ITransferable extends ITickable
{
    @Override
    public default void update()
    {
        TileEntity tile = (TileEntity) this;
        if (tile.getWorld() != null && !tile.getWorld().isRemote)
        {
            setTransferCooldown(getTransferCooldown() - 1);
            if (!isTransferCooldown())
            {
                setTransferCooldown(0);
                boolean flag = false;
                if (canTransferOut())
                    flag = transferOut();
                if (canTransferIn())
                    flag = transferIn() || flag;
                if (flag)
                {
                    resetTransferCooldown();
                    tile.markDirty();
                }
            }
        }
    }
    
    public default int getRate()
    {
        return 8;
    }
    
    public int getTransferCooldown();
    
    public void setTransferCooldown(int cooldown);
    
    public default void resetTransferCooldown()
    {
        setTransferCooldown(getRate());
    }
    
    public default boolean isTransferCooldown()
    {
        return getTransferCooldown() > 0;
    }
    
    public default boolean canTransferOut()
    {
        return true;
    }
    
    public default boolean canTransferIn()
    {
        return true;
    }
    
    public default boolean transferOut()
    {
        return false;
    }
    
    public default boolean transferIn()
    {
        return false;
    }

    public default NBTTagCompound writeTransferCooldown(NBTTagCompound compound)
    {
        compound.setInteger("transferCooldown", getTransferCooldown());
        return compound;
    }

    public default void readTransferCooldown(NBTTagCompound compound)
    {
        setTransferCooldown(compound.getInteger("transferCooldown"));
    }
}
