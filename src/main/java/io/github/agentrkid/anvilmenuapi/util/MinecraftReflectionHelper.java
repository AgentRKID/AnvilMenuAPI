package io.github.agentrkid.anvilmenuapi.util;

import com.comphenix.protocol.utility.MinecraftReflection;

public class MinecraftReflectionHelper {

    public static Class<?> fromClassName(String name) {
        try {
            return Class.forName("net.minecraft.server." + MinecraftReflection.getPackageVersion() + "." + name);
        } catch (ClassNotFoundException exception) {
            return null;
        }
    }

}
