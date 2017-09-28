package com.jtripled.voxen.item;

import com.jtripled.voxen.registry.RegistrationHandler;

/**
 *
 * @author jtripled
 */
public interface ItemBase
{
    public String getName();
    
    public default void registerItem(RegistrationHandler registry)
    {
        registry.registerItem(this);
    }

    public default void registerRenderer(RegistrationHandler registry)
    {
        registry.registerItemRenderer(this, getName());
    }
}
