package dev.dynxmic.dynamicbedwars.util;

import dev.dynxmic.dynamicbedwars.DynamicBedwars;
import dev.dynxmic.dynamicbedwars.game.GameHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class PluginUtils {

    public static DynamicBedwars getPlugin() {
        return DynamicBedwars.getPlugin(DynamicBedwars.class);
    }

    public static void registerEvent(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    public static GameHandler getGameHandler() {
        return getPlugin().getGameHandler();
    }

}
