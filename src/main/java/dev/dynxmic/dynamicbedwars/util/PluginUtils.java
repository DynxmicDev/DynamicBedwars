package dev.dynxmic.dynamicbedwars.util;

import com.comphenix.protocol.ProtocolManager;
import dev.dynxmic.dynamicbedwars.DynamicBedwars;
import dev.dynxmic.dynamicbedwars.game.DeathHandler;
import dev.dynxmic.dynamicbedwars.game.GameHandler;

public class PluginUtils {

    public static DynamicBedwars getPlugin() {
        return DynamicBedwars.getPlugin(DynamicBedwars.class);
    }

    public static GameHandler getGameHandler() {
        return getPlugin().getGameHandler();
    }

    public static DeathHandler getDeathHandler() {
        return getPlugin().getDeathHandler();
    }

    public static ProtocolManager getProtocolHandler() {
        return getPlugin().getProtocolHandler();
    }

}
