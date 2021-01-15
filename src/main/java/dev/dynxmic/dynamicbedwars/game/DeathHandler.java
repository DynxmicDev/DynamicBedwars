package dev.dynxmic.dynamicbedwars.game;

import dev.dynxmic.dynamicbedwars.util.ChatUtils;
import dev.dynxmic.dynamicbedwars.util.PlayerUtils;
import dev.dynxmic.dynamicbedwars.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class DeathHandler {

    private final Map<Player, Player> damagers;
    private final Map<Player, Integer> tasks;
    private final int taskID;

    public DeathHandler() {
        this.damagers = new HashMap<>();
        this.tasks = new HashMap<>();

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Kill Player If In Void
                    if (PluginUtils.getGameHandler().getPlayerState(player.getUniqueId()).equals(PlayerState.ALIVE) && player.getLocation().getBlockY() <= -50) {
                        handleDeath(player, EntityDamageEvent.DamageCause.VOID);
                    }
                }
            }

        }.runTaskTimer(PluginUtils.getPlugin(), 0, 5); // Run 4 Times Per Second (20 Ticks = 1 Second)
        taskID = task.getTaskId();  // Set Void Check Task ID
    }

    public void handleDamage(Player player, Player damager) {
        // Cancel Running Damager Task When Latest Damage Applied
        if (tasks.containsKey(player)) Bukkit.getScheduler().cancelTask(tasks.get(player));

        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                // Remove Latest Damager && Remove Latest Damager Task ID
                damagers.remove(player);
                tasks.remove(player);
            }

        }.runTaskLater(PluginUtils.getPlugin(), 200);  // Run 10 Seconds Later (20 Ticks = 1 Second)

        // Set Latest Damager && Set Damager Task ID
        damagers.put(player, damager);
        tasks.put(player, task.getTaskId());
    }

    public void handleDeath(Player player, EntityDamageEvent.DamageCause cause) {
        switch (cause) {
            case ENTITY_ATTACK:
                if (damagers.containsKey(player)) {
                    // If Damaged In Last 10 Seconds (See Above)
                    ChatUtils.broadcast(player.getDisplayName() + "&7 was killed by " + damagers.get(player).getDisplayName() + "&7.");
                    break;
                }
            case VOID:
                if (damagers.containsKey(player)) {
                    // Knocked In The Void (Damaged In last 10 Seconds)
                    ChatUtils.broadcast(player.getDisplayName() + " &7 was knocked in the void by " + damagers.get(player).getDisplayName() + "&7.");
                }
                else {
                    // Otherwise Fell Into Void On Their Own
                    ChatUtils.broadcast(player.getDisplayName() + " &7fell into the void.");
                }
                break;
            default:
                // Fall Damage, Drowning etc
                ChatUtils.broadcast(player.getDisplayName() + " &7died.");
        }

        // TODO Respawn Player
        PlayerUtils.prepare(player, GameMode.SURVIVAL, new ItemStack[]{}, new ItemStack[]{}, PlayerState.ALIVE);
    }

    public void killProcess() {
        // Cancel Void Check
        Bukkit.getScheduler().cancelTask(taskID);
    }

}
