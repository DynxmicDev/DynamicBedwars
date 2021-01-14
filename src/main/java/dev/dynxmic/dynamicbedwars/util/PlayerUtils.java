package dev.dynxmic.dynamicbedwars.util;

import dev.dynxmic.dynamicbedwars.game.BedwarsTeam;
import dev.dynxmic.dynamicbedwars.game.GameHandler;
import dev.dynxmic.dynamicbedwars.game.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerUtils {

    public static void teleport(UUID player, String world, double x, double y, double z) {
        if (Bukkit.getOnlinePlayers().stream().noneMatch(onlinePlayer -> onlinePlayer.getUniqueId().equals(player))) return;
        Bukkit.getPlayer(player).teleport(new Location(Bukkit.getWorld(world), x, y, z));
    }

    public static void setGameMode(UUID player, GameMode gamemode) {
        if (Bukkit.getOnlinePlayers().stream().noneMatch(onlinePlayer -> onlinePlayer.getUniqueId().equals(player))) return;
        Bukkit.getPlayer(player).setGameMode(gamemode);
    }

    public static void setDisplayName(UUID player, String name) {
        if (Bukkit.getOnlinePlayers().stream().noneMatch(onlinePlayer -> onlinePlayer.getUniqueId().equals(player))) return;
        Bukkit.getPlayer(player).setDisplayName(ChatUtils.parse(name));
    }

    public static String getName(UUID player) {
        if (Bukkit.getOnlinePlayers().stream().noneMatch(onlinePlayer -> onlinePlayer.getUniqueId().equals(player))) return "";
        return Bukkit.getPlayer(player).getName();
    }

    public static void prepare(Player player, GameMode gamemode, ItemStack[] inventory, ItemStack[] armour, PlayerState state) {
        GameHandler handler = PluginUtils.getGameHandler();
        BedwarsTeam team = handler.getPlayerTeam(player.getUniqueId());

        handler.setPlayerState(player.getUniqueId(), state);
        player.setGameMode(gamemode);
        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armour);
        player.teleport(new Location(Bukkit.getWorld("world"), team.getX(), team.getY(), team.getZ()));

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

                for (Player craftPlayer : Bukkit.getOnlinePlayers()) {

                }

                break;
            default:
                displayName = "";
                tabName = "";
        }

        player.setPlayerListName(ChatUtils.parse(tabName));
        player.setDisplayName(ChatUtils.parse(displayName));
    }

}
