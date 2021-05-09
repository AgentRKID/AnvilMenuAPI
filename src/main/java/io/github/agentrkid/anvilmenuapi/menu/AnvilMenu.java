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
import java.util.function.Consumer;

public class AnvilMenu {
    private static final String ANVIL_INVENTORY_NAME = "minecraft:anvil";
    private static final WrappedChatComponent EMPTY_CHAT_COMPONENT = WrappedChatComponent.fromText("EMPTY");

    public static Map<UUID, AnvilMenu> openedMenus = new HashMap<>();

    private final ItemStack defaultStack = new ItemStack(Material.PAPER);

    @Getter private final Consumer<String> finishConsumer;
    @Getter private Consumer<Player> closeConsumer;

    public AnvilMenu(Consumer<String> finishConsumer) {
        this.finishConsumer = finishConsumer;
    }

    public AnvilMenu(Consumer<String> finishConsumer, Consumer<Player> closeConsumer) {
        this.finishConsumer = finishConsumer;
        this.closeConsumer = closeConsumer;
    }

    /**
     * Opens a anvil GUI
     *
     * @param player the player to open for.
     * @param defaultText the default text of the paper.
     */
    public void open(Player player, String defaultText) {
        PacketContainer openAnvilPacket = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);

        openAnvilPacket.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);
        openAnvilPacket.getStrings().write(0, ANVIL_INVENTORY_NAME);
        openAnvilPacket.getChatComponents().write(0, EMPTY_CHAT_COMPONENT);
        openAnvilPacket.getIntegers().write(1, 0);

        ItemMeta meta = defaultStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', defaultText));
        defaultStack.setItemMeta(meta);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, openAnvilPacket);
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
        PacketContainer closeAnvilPacket = new PacketContainer(PacketType.Play.Server.CLOSE_WINDOW);

        closeAnvilPacket.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, closeAnvilPacket);

            if (closeConsumer != null) {
                closeConsumer.accept(player);
            }
        } catch (Exception ignored) {}
    }

    /**
     * Sends our paper back to the anvil
     *
     * @param player the player to send the packet to.
     * @throws InvocationTargetException Protocol Lib throws this.
     */
    public void sendPaperBack(Player player) throws InvocationTargetException {
        PacketContainer addPaperPacket = new PacketContainer(PacketType.Play.Server.SET_SLOT);

        addPaperPacket.getIntegers().write(0, AnvilMenuAPI.ANVIL_MENU_ID);
        addPaperPacket.getIntegers().write(1, 0);
        addPaperPacket.getItemModifier().write(0, defaultStack);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, addPaperPacket);
    }
}
