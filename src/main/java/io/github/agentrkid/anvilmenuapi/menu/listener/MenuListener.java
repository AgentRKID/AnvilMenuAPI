package io.github.agentrkid.anvilmenuapi.menu.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.agentrkid.anvilmenuapi.AnvilMenuAPI;
import io.github.agentrkid.anvilmenuapi.menu.AnvilMenu;
import io.github.agentrkid.anvilmenuapi.menu.CloseResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MenuListener extends PacketAdapter implements Listener {
    public MenuListener() {
        super(AnvilMenuAPI.getInstance(), PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CLOSE_WINDOW);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        PacketType packetType = event.getPacketType();
        Player player = event.getPlayer();

        int windowId = packet.getIntegers().read(0);

        if (windowId == AnvilMenuAPI.ANVIL_MENU_ID) {
            AnvilMenu menu = AnvilMenu.openedMenus.get(player.getUniqueId());

            if (menu != null) {
                if (packetType == PacketType.Play.Client.WINDOW_CLICK) {
                    int slotId = packet.getIntegers().read(1);

                    if (slotId != 2) {
                        try {
                            // Since minecraft is a little snowflake
                            // it removes our paper... so we need to add it back.
                            menu.sendPaperBack(player);
                        } catch (Exception ignored) {}
                    } else {
                        // Handle the finished product by accepting the finish function.
                        menu.getAnvilConsumer().accept(CloseResult.FINISH, packet.getItemModifier().read(0).getItemMeta().getDisplayName());

                        // Minecraft for some reason won't close
                        // the inventory after so we need to do it.
                        menu.close(player);
                    }
                } else {
                    // They closed without giving a result,
                    // lets notify the consumer they did!
                    menu.getAnvilConsumer().accept(CloseResult.PLAYER, "");
                    AnvilMenu.openedMenus.remove(player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AnvilMenu.openedMenus.remove(event.getPlayer().getUniqueId());
    }
}
