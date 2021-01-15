package dev.dynxmic.dynamicbedwars.listener;

import dev.dynxmic.dynamicbedwars.game.GameState;
import dev.dynxmic.dynamicbedwars.util.ChatUtils;
import dev.dynxmic.dynamicbedwars.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Handle Player Join
        PluginUtils.getGameHandler().handleLogin(event);
    }

}
