package dev.dynxmic.dynamicbedwars.model;

import dev.dynxmic.dynamicbedwars.util.PluginUtils;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Countdown {

    private int time;

    public Countdown(int countdown) {
        time = countdown;
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!proceed())

                if (time == 0) {
                    cancel();
                    end();
                    return;
                }

                count(time);
                time--;
            }

        }.runTaskTimer(PluginUtils.getPlugin(), 0L, 20L);
    }

    public abstract void end();

    public abstract void count(int time);

    public abstract boolean proceed();

}
