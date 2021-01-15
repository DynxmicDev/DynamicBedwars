package dev.dynxmic.dynamicbedwars.listener;

import dev.dynxmic.dynamicbedwars.game.GameHandler;
import dev.dynxmic.dynamicbedwars.game.PlayerState;
import dev.dynxmic.dynamicbedwars.util.PluginUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            event.setCancelled(true);
            return;
        }

        if (((Player) event.getEntity()).getHealth() <= event.getFinalDamage()) {
            PluginUtils.getDeathHandler().handleDeath(player, event.getCause());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        GameHandler handler = PluginUtils.getGameHandler();
        if (!handler.getPlayerState(player.getUniqueId()).equals(PlayerState.ALIVE) || !handler.getPlayerState(damager.getUniqueId()).equals(PlayerState.ALIVE)) {
            event.setCancelled(true);
        }

        if (handler.getPlayerTeam(player.getUniqueId()).equals(handler.getPlayerTeam(damager.getUniqueId()))) {
            event.setCancelled(true);
        }

        PluginUtils.getDeathHandler().handleDamage((Player) event.getEntity(), (Player) event.getDamager());
    }

}
