package io.github.agentrkid.anvilmenuapi.util;

import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.wrappers.AbstractWrapper;

/**
 * Wrapper for the minecraft class: PacketDataSerializer as ProtocolLib
 * doesn't have its own wrapper for this
 * (Only custom payloads actually have a variable for this, so that might be the reason)
 */
public class WrappedPacketDataSerializer extends AbstractWrapper {

    private static final Class<?> packetDataSerializer = MinecraftReflectionHelper.fromClassName("PacketDataSerializer");
    private static final MethodAccessor readStringAccessor;

    static {
        readStringAccessor = Accessors.getMethodAccessor(packetDataSerializer, "c", int.class);
    }

    public WrappedPacketDataSerializer(Object handle) {
        super(packetDataSerializer);
        setHandle(handle);
    }

    public static WrappedPacketDataSerializer fromHandle(Object handle) {
        return new WrappedPacketDataSerializer(handle);
    }

    public String readString(int maxBuffer) {
        return (String) readStringAccessor.invoke(handle, maxBuffer);
    }

}
