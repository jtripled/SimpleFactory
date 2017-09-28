package com.jtripled.voxen.entity;

import com.google.common.collect.Lists;
import com.jtripled.voxen.registry.RegistrationHandler;
import java.util.List;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 *
 * @author jtripled
 */
public interface MobBase extends EntityBase
{
    @Override
    public Class<? extends EntityLiving> getEntityClass();
    
    public default int getEggPrimary()
    {
        return -1;
    }

    public default int getEggSecondary()
    {
        return -1;
    }

    public default boolean canSpawn(Biome biome)
    {
        return false;
    }

    public default int getSpawnRate()
    {
        return 4;
    }

    public default int getSpawnMin()
    {
        return 2;
    }

    public default int getSpawnMax()
    {
        return 3;
    }

    public default EnumCreatureType getSpawnType()
    {
        return EnumCreatureType.CREATURE;
    }
    
    @Override
    public default void registerEntity(RegistrationHandler handler)
    {
        EntityEntry registration = new EntityEntry(getEntityClass(), getName());
        registration.setRegistryName(getResourceLocation());
        if (getEggPrimary() != -1)
            registration.setEgg(new EntityList.EntityEggInfo(getResourceLocation(), getEggPrimary(), getEggSecondary()));
        handler.registerEntity(registration);
    }
    
    public default void registerSpawn()
    {
        List<Biome> biomes = Lists.newArrayList();
        for (Biome biome : Biome.REGISTRY)
            if (canSpawn(biome))
                biomes.add(biome);
        if (!biomes.isEmpty())
            EntityRegistry.addSpawn(getEntityClass(), getSpawnRate(), getSpawnMin(), getSpawnMax(), getSpawnType(), biomes.toArray(new Biome[biomes.size()]));
    }
}
