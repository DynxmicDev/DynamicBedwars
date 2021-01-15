package dev.dynxmic.dynamicbedwars.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {

    public static String parse(String message) {
        // Parse Chat Message Colour Codes
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void send(CommandSender sender, String message) {
        // Send Message To CommandSender
        sender.sendMessage(parse(message));
    }

    public static void sendConsole(String message) {
        // Send Message To Console
        send(Bukkit.getConsoleSender(), message);
    }

    public static void broadcast(String message) {
        // Broadcast Message
        Bukkit.broadcastMessage(parse(message));
    }

    public static void broadcastSound(Sound sound) {
        // Broadcast Sound
        for (Player player : Bukkit.getOnlinePlayers()) player.playSound(player.getLocation(), sound, 1L, 1L);
    }

    public static void broadcastTitle(String title) {
        // Broadcast Title
        for (Player player : Bukkit.getOnlinePlayers()) PlayerUtils.sendTitle(player, parse(title), 0, 20, 0);
    }

}
