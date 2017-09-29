package com.jtripled.voxen.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import com.jtripled.voxen.block.IBlockBase;

/**
 *
 * @author jtripled
 */
public class ItemBlockBase extends ItemBlock implements ItemBase
{
    private final IBlockBase blockBase;
    
    public ItemBlockBase(IBlockBase block)
    {
        super((Block) block);
        this.blockBase = block;
    }

    @Override
    public String getName()
    {
        return blockBase.getName();
    }
}
