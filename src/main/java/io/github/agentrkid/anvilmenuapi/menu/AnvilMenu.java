package io.github.agentrkid.anvilmenuapi.menu;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
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
import java.util.function.BiConsumer;

public class AnvilMenu {

    private static final String ANVIL_INVENTORY_NAME = "minecraft:anvil";
    private static final WrappedChatComponent EMPTY_CHAT_COMPONENT = WrappedChatComponent.fromText("EMPTY");

    private static final PacketContainer OPEN_MENU_PACKET;
    private static final PacketContainer CLOSE_MENU_PACKET;

    private static final PacketContainer CARRIED_ITEM_PACKET;

    public static Map<UUID, AnvilMenu> openedMenus = new HashMap<>();

    private final ItemStack defaultStack = new ItemStack(Material.PAPER);
    @Getter private final Map<UUID, String> inputs = new HashMap<>();

    private PacketContainer setAnvilSlotPacket;

    @Getter private final BiConsumer<CloseResult, String>  consumer;

    public AnvilMenu(BiConsumer<CloseResult, String> consumer) {
        this.consumer = consumer;
    }

    /**
     * Opens a anvil GUI for {@param player} with a paper
     * that has the text from {@param defaultText}
     */
    public void open(Player player, String defaultText) {
        ItemMeta meta = defaultStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', defaultText));
        defaultStack.setItemMeta(meta);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, OPEN_MENU_PACKET);
            sendPaperAndClearCursor(player);

            openedMenus.put(player.getUniqueId(), this);
        } catch (Exception ignored) {}
    }

    /**
     * Closes the inventory of the anvil for {@param player}.
     */
    public void close(Player player) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, CLOSE_MENU_PACKET);
            inputs.remove(player.getUniqueId());
            AnvilMenu.openedMenus.remove(player.getUniqueId());
        } catch (Exception ignored) {}
    }

    /**
     * Sends our paper back to the anvil for {@param player}
     * @throws InvocationTargetException Protocol Lib throws this.
     */
    public void sendPaperAndClearCursor(Player player) throws InvocationTargetException {
        if (setAnvilSlotPacket == null) {
            setAnvilSlotPacket = new PacketContainer(PacketType.Play.Server.SET_SLOT);

            setAnvilSlotPacket.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);
            setAnvilSlotPacket.getIntegers().write(1, 0);
            setAnvilSlotPacket.getItemModifier().write(0, defaultStack);
        }

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.sendServerPacket(player, setAnvilSlotPacket);
        protocolManager.sendServerPacket(player, CARRIED_ITEM_PACKET);
    }

    static {
        CLOSE_MENU_PACKET = new PacketContainer(PacketType.Play.Server.CLOSE_WINDOW);
        CLOSE_MENU_PACKET.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);

        OPEN_MENU_PACKET = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
        OPEN_MENU_PACKET.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);
        OPEN_MENU_PACKET.getStrings().write(0, ANVIL_INVENTORY_NAME);
        OPEN_MENU_PACKET.getChatComponents().write(0, EMPTY_CHAT_COMPONENT);
        OPEN_MENU_PACKET.getIntegers().write(1, 0);

        CARRIED_ITEM_PACKET = new PacketContainer(PacketType.Play.Server.SET_SLOT);
        CARRIED_ITEM_PACKET.getIntegers().write(0, -1);
        CARRIED_ITEM_PACKET.getIntegers().write(1, -1);
        CARRIED_ITEM_PACKET.getItemModifier().write(0, null);
    }

}
