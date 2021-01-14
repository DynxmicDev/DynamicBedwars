package dev.dynxmic.dynamicbedwars.game;

import org.bukkit.GameMode;

public enum BedwarsTeam {

    RED(-84.5, 65, 26.5, "&c"),
    // BLUE,
    // GREEN,
    // YELLOW,
    // AQUA,
    // WHITE,
    // PINK,
    // GRAY,
    SPECTATOR(0.5, 119, 0.5, "&7");

    private final double x;
    private final double y;
    private final double z;
    private final String colour;

    BedwarsTeam(double x, double y, double z, String colour) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.colour = colour;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getColour() {
        return colour;
    }

}
