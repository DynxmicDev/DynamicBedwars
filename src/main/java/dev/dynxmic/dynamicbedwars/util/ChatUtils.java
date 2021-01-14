package dev.dynxmic.dynamicbedwars.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtils {

    public static String parse(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(parse(message));
    }

    public static void sendConsole(String message) {
        send(Bukkit.getConsoleSender(), message);
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(parse(message));
    }

}
