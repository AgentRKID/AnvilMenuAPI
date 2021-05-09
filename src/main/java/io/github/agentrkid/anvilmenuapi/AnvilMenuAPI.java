package io.github.agentrkid.anvilmenuapi;

import com.comphenix.protocol.ProtocolLibrary;
import io.github.agentrkid.anvilmenuapi.menu.listener.MenuListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilMenuAPI extends JavaPlugin implements Listener {
    @Getter private static AnvilMenuAPI instance;

    // We use a specific ID which is 0 and it will always return 0,
    // we couldn't do anything else since minecraft sends *bytes*
    // instead of integers, so it will transform it unless its 0,
    // minecraft will never have a container id of  0.
    public static int ANVIL_MENU_ID = 0;

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(this, this);
        ProtocolLibrary.getProtocolManager().addPacketListener(new MenuListener());
    }

    @Override
    public void onDisable() {

    }
}
