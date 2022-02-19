package io.github.agentrkid.anvilmenuapi;

import com.comphenix.protocol.ProtocolLibrary;
import io.github.agentrkid.anvilmenuapi.menu.listener.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilMenuAPI {

    private static boolean enabled = false;
    private static JavaPlugin enablingPlugin = null;

    // We use a specific ID which is 0 and it will always return 0,
    // we couldn't do anything else since minecraft sends *bytes*
    // instead of integers, so it will transform it unless its 0,
    // minecraft will never have a container id of  0.
    public static int ANVIL_MENU_ID = 0;


    public static void register(JavaPlugin plugin) throws IllegalStateException {
        if (enabled) {
            throw new IllegalStateException(enablingPlugin.getName() + " has already enabled AnvilMenuAPI!");
        }

        MenuListener listener = new MenuListener(plugin);

        ProtocolLibrary.getProtocolManager().addPacketListener(listener);
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        enablingPlugin = plugin;
        enabled = true;
    }

}
