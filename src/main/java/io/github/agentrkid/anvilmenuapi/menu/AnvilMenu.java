package io.github.agentrkid.anvilmenuapi.menu;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.github.agentrkid.anvilmenuapi.AnvilMenuAPI;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnvilMenu {
    private static final String ANVIL_INVENTORY_NAME = "minecraft:anvil";
    private static final WrappedChatComponent EMPTY_CHAT_COMPONENT = WrappedChatComponent.fromText("EMPTY");

    private static final PacketContainer OPEN_MENU_PACKET;
    private static final PacketContainer CLOSE_MENU_PACKET;

    public static Map<UUID, AnvilMenu> openedMenus = new HashMap<>();

    private final ItemStack defaultStack = new ItemStack(Material.PAPER);

    private PacketContainer setSlotPacket;

    @Getter private final AnvilConsumer anvilConsumer;

    public AnvilMenu(AnvilConsumer anvilConsumer) {
        this.anvilConsumer = anvilConsumer;
    }

    /**
     * Opens a anvil GUI
     *
     * @param player the player to open for.
     * @param defaultText the default text of the paper.
     */
    public void open(Player player, String defaultText) {
        ItemMeta meta = defaultStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', defaultText));
        defaultStack.setItemMeta(meta);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, OPEN_MENU_PACKET);
            sendPaperBack(player);

            openedMenus.put(player.getUniqueId(), this);
        } catch (Exception ignored) {}
    }

    /**
     * Closes the inventory of the anvil.
     *
     * @param player the player to close for.
     */
    public void close(Player player) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, CLOSE_MENU_PACKET);
            AnvilMenu.openedMenus.remove(player.getUniqueId());
        } catch (Exception ignored) {}
    }

    /**
     * Sends our paper back to the anvil
     *
     * @param player the player to send the packet to.
     * @throws InvocationTargetException Protocol Lib throws this.
     */
    public void sendPaperBack(Player player) throws InvocationTargetException {
        if (setSlotPacket == null) {
            setSlotPacket = new PacketContainer(PacketType.Play.Server.SET_SLOT);

            setSlotPacket.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);
            setSlotPacket.getIntegers().write(1, 0);
            setSlotPacket.getItemModifier().write(0, defaultStack);
        }

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, setSlotPacket);
    }

    static {
        CLOSE_MENU_PACKET = new PacketContainer(PacketType.Play.Server.CLOSE_WINDOW);
        CLOSE_MENU_PACKET.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);

        OPEN_MENU_PACKET = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
        OPEN_MENU_PACKET.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);
        OPEN_MENU_PACKET.getStrings().write(0, ANVIL_INVENTORY_NAME);
        OPEN_MENU_PACKET.getChatComponents().write(0, EMPTY_CHAT_COMPONENT);
        OPEN_MENU_PACKET.getIntegers().write(1, 0);
    }
}
