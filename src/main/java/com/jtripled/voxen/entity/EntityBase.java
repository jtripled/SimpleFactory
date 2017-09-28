package com.jtripled.voxen.entity;

import com.jtripled.voxen.mod.VoxenMod;
import com.jtripled.voxen.registry.RegistrationHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.registry.EntityEntry;

/**
 *
 * @author jtripled
 */
public interface EntityBase
{
    public Class<? extends Entity> getEntityClass();

    public IRenderFactory getRenderFactory();

    public String getName();

    public ResourceLocation getResourceLocation();
    
    public default void registerEntity(RegistrationHandler handler)
    {
        EntityEntry registration = new EntityEntry(getEntityClass(), getName());
        registration.setRegistryName(getResourceLocation());
        handler.registerEntity(registration);
    }

    public default void registerRenderer(RegistrationHandler handler)
    {
        handler.registerEntityRenderer(getEntityClass(), getRenderFactory());
    }
}
