package dev.dynxmic.dynamicbedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerLossListener implements Listener {

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event) {
        if (event.getFoodLevel() != 20) event.setFoodLevel(20);
        event.setCancelled(true);
    }

}
