package dev.dynxmic.dynamicbedwars;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.dynxmic.dynamicbedwars.game.DeathHandler;
import dev.dynxmic.dynamicbedwars.game.GameHandler;
import dev.dynxmic.dynamicbedwars.listener.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class DynamicBedwars extends JavaPlugin {

    private GameHandler gameHandler;
    private DeathHandler deathHandler;
    private ProtocolManager protocolHandler;

    @Override
    public void onEnable() {
        gameHandler = new GameHandler(1, 2, 2);
        deathHandler = new DeathHandler();
        protocolHandler = ProtocolLibrary.getProtocolManager();

        registerListener(new PlayerJoinListener());
        registerListener(new HungerLossListener());
        registerListener(new PlayerQuitListener());
        registerListener(new BlockBreakListener());
        registerListener(new BlockPlaceListener());
        registerListener(new EntityDamageListener());
        registerListener(new MobSpawnListener());
    }

    @Override
    public void onDisable() {
        deathHandler.killProcess();
    }

    private void registerListener(Listener listener) {
        // Register Given Event Listener
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public DeathHandler getDeathHandler() {
        return deathHandler;
    }

    public ProtocolManager getProtocolHandler() {
        return protocolHandler;
    }

}
