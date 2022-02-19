package io.github.agentrkid.anvilmenuapi.menu.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.agentrkid.anvilmenuapi.AnvilMenuAPI;
import io.github.agentrkid.anvilmenuapi.menu.AnvilMenu;
import io.github.agentrkid.anvilmenuapi.menu.CloseResult;
import io.github.agentrkid.anvilmenuapi.util.WrappedPacketDataSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MenuListener extends PacketAdapter implements Listener {

    public MenuListener(JavaPlugin plugin) {
        super(plugin, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.CUSTOM_PAYLOAD, PacketType.Play.Client.CLOSE_WINDOW);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        PacketType packetType = event.getPacketType();
        Player player = event.getPlayer();

        if (packetType == PacketType.Play.Client.WINDOW_CLICK || packetType == PacketType.Play.Client.CLOSE_WINDOW) {
            int windowId = packet.getIntegers().read(0);

            if (windowId == AnvilMenuAPI.ANVIL_MENU_ID) {
                AnvilMenu menu = AnvilMenu.openedMenus.get(player.getUniqueId());

                if (menu != null) {
                    if (packetType == PacketType.Play.Client.WINDOW_CLICK) {
                        int slotId = packet.getIntegers().read(1);

                        // Slot 2 is finish/repair
                        // Slot 1 is the item to repair slot 0 with
                        // Slot 0 is the input item
                        if (slotId != 2) {
                            try {
                                // Since minecraft is a little snowflake
                                // it removes our paper... so we need to add it back.
                                menu.sendPaperAndClearCursor(player);
                            } catch (Exception ignored) { }
                        } else {
                            // Handle the finished product by accepting the finish function.
                            // Inputs should always be filled as the client sends MC|ItemName once the GUI is open, edited, etc.
                            menu.getConsumer().accept(CloseResult.FINISH, menu.getInputs().get(player.getUniqueId()));

                            // Minecraft for some reason won't close
                            // the inventory after so we need to do it.
                            menu.close(player);
                        }

                        // No reason for the server to handle the packet.
                        event.setCancelled(true);
                    } else {
                        // They closed without giving a result,
                        // lets notify the consumer they did!
                        menu.getConsumer().accept(CloseResult.PLAYER, "");
                        menu.getInputs().remove(player.getUniqueId());
                        AnvilMenu.openedMenus.remove(player.getUniqueId());
                    }
                }
            }
        } else {
            String channel = packet.getStrings().read(0);

            // We will only handle from this channel, as this is the only thing we need to listen for.
            if (channel.equals("MC|ItemName")) {
                AnvilMenu menu = AnvilMenu.openedMenus.get(player.getUniqueId());

                // Make sure this is cause of our menu.
                if (menu != null) {
                    try {
                        WrappedPacketDataSerializer packetSerializer = WrappedPacketDataSerializer.fromHandle(packet.getModifier().read(1));
                        menu.getInputs().put(player.getUniqueId(), packetSerializer.readString(32767));
                    } catch (Exception ex) {
                        player.sendMessage(ChatColor.RED + "Failed to read input.");
                    }

                    // The server doesn't need this.
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AnvilMenu.openedMenus.remove(event.getPlayer().getUniqueId());
    }

}
