package dev.dynxmic.dynamicbedwars.listener;

import dev.dynxmic.dynamicbedwars.util.PluginUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockPlaceEvent event) {
        PluginUtils.getGameHandler().handleBlockPlace(event);
    }

}
