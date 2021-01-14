package dev.dynxmic.dynamicbedwars;

import dev.dynxmic.dynamicbedwars.game.GameHandler;
import dev.dynxmic.dynamicbedwars.listener.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicBedwars extends JavaPlugin {

    private GameHandler gameHandler;

    @Override
    public void onEnable() {
        gameHandler = new GameHandler(1, 1);
        registerListener(new PlayerJoinListener());
        registerListener(new HungerLossListener());
        registerListener(new PlayerQuitListener());
        registerListener(new BlockBreakListener());
        registerListener(new BlockPlaceListener());
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {

    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

}
