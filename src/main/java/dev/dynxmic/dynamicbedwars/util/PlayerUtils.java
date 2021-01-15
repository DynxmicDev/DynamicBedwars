package dev.dynxmic.dynamicbedwars.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.dynxmic.dynamicbedwars.game.BedwarsTeam;
import dev.dynxmic.dynamicbedwars.game.GameHandler;
import dev.dynxmic.dynamicbedwars.game.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class PlayerUtils {

    public static void prepare(Player player, GameMode gamemode, ItemStack[] inventory, ItemStack[] armour, PlayerState state) {
        // Prepare Player (Set Gamemode, Inventory and Teleport Home)
        GameHandler handler = PluginUtils.getGameHandler();
        BedwarsTeam team = handler.getPlayerTeam(player.getUniqueId());

        handler.setPlayerState(player.getUniqueId(), state);
        player.setGameMode(gamemode);
        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armour);
        player.setFallDistance(0);
        player.teleport(new Location(Bukkit.getWorld("world"), team.getX(), team.getY(), team.getZ()));
        player.setHealth(20);

        // Set Tab And Display Names
        String tabName;
        String displayName;
        switch (state) {
            case WAITING:
                displayName = "&7" + player.getName();
                tabName = displayName;
                break;
            case ALIVE:
                displayName = team.getColour() + player.getName();
                tabName = team.getColour() + "&l" + team.toString().charAt(0) +  " " + displayName;
                System.out.println(tabName);
                break;
            case SPECTATOR:
                displayName = "&7" + player.getName();
                tabName = "&7&lSPEC &7" + displayName;

                break;
            default:
                displayName = "";
                tabName = "";
        }

        player.setPlayerListName(ChatUtils.parse(tabName));
        player.setDisplayName(ChatUtils.parse(displayName));
    }

    public static void sendTitle(Player player, String title, int in, int stay, int out) {
        ProtocolManager handler = PluginUtils.getProtocolHandler();

        // Create Title Timings Packet
        PacketContainer timings = handler.createPacket(PacketType.Play.Server.TITLE);
        timings.getTitleActions().write(0, EnumWrappers.TitleAction.TIMES);
        timings.getIntegers().write(0, in);
        timings.getIntegers().write(1, stay);
        timings.getIntegers().write(2, out);

        // Create Title Packet
        PacketContainer packet = handler.createPacket(PacketType.Play.Server.TITLE);
        packet.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(title));

        try {
            // Send Title Packets
            handler.sendServerPacket(player, timings);
            handler.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
