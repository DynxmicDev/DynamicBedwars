package dev.dynxmic.dynamicbedwars.listener;

import dev.dynxmic.dynamicbedwars.util.PluginUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check Block Breaks
        PluginUtils.getGameHandler().handleBlockBreak(event);
    }

}
