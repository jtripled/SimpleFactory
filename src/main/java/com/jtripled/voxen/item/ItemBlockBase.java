package com.jtripled.voxen.item;

import com.jtripled.voxen.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 *
 * @author jtripled
 */
public class ItemBlockBase extends ItemBlock implements ItemBase
{
    private final BlockBase blockBase;
    
    public ItemBlockBase(BlockBase block)
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
