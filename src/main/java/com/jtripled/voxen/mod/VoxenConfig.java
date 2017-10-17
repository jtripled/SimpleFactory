package com.jtripled.voxen.mod;

import com.jtripled.simplefactory.SimpleFactoryRegistry;
import com.jtripled.voxen.registry.Registry;

/**
 *
 * @author jtripled
 */
public class VoxenConfig
{
    public static final String ID = "simplefactory";
    public static final String NAME = "SimpleFactory";
    public static final String DESCRIPTION = "Simple description.";
    public static final String VERSION = "0.1";
    public static final Registry REGISTRY = new SimpleFactoryRegistry();
}
