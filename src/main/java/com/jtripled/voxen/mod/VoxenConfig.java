package com.jtripled.voxen.mod;

import com.jtripled.simplefactory.SimpleFactoryRegistrationHandler;
import com.jtripled.voxen.registry.RegistrationHandler;

/**
 *
 * @author jtripled
 */
public class VoxenConfig
{
    public static final String ID = "simplefactory";
    public static final String NAME = "SimpleFactory";
    public static final String DESCRIPTION = "Simple description.";
    public static final String VERSION = "1.12.2";
    public static final RegistrationHandler REGISTRATION_HANDLER = new SimpleFactoryRegistrationHandler();
}
