package dev.dynxmic.dynamicbedwars.game;

import dev.dynxmic.dynamicbedwars.util.ChatUtils;
import dev.dynxmic.dynamicbedwars.util.PlayerUtils;
import dev.dynxmic.dynamicbedwars.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameHandler {

    private GameState state;
    private int countdown;
    private int countdownID;

    private final int teamSize;
    private final int required;

    private final Map<UUID, PlayerState> players;
    private final Map<BedwarsTeam, List<UUID>> teams;
    private final List<GameParty> parties;
    private final List<Location> placedBlocks;

    public GameHandler(int teamSize, int required) {
        this.players = new HashMap<>();
        this.teams = new HashMap<>();
        this.parties = new ArrayList<>();
        this.placedBlocks = new ArrayList<>();
        this.countdownID = 0;
        this.state = GameState.WAITING;
        this.teamSize = teamSize;
        this.required = required;

        for (BedwarsTeam team : BedwarsTeam.values()) teams.put(team, new ArrayList<>());
    }

    private void startCountdown() {
        if (countdownID != 0) return;
        countdown = 20;
        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                if (countdown == 0) {
                    cancel();
                    startGame();
                    return;
                }

                if (countdown == 20) broadcastStart("&e", countdown);
                if (countdown == 10) broadcastStart("&6", countdown);
                if (countdown < 6) broadcastStart("&c", countdown);
                countdown--;
            }

        }.runTaskTimer(PluginUtils.getPlugin(), 0L, 20L);
        countdownID = task.getTaskId();
    }

    public void cancelCountdown() {
        if (countdown == 0) return;
        Bukkit.getScheduler().cancelTask(countdownID);
        ChatUtils.broadcast("&cWe don't have enough players! Start cancelled.");
        countdownID = 0;
    }

    private void broadcastStart(String colour, int count) {
        ChatUtils.broadcast("&eThe game starts in " + colour + count + " &eseconds!");
    }

    private void startGame() {
        state = GameState.PLAYING;

        // Calculate Party Teams
        for (GameParty party : parties) {
            BedwarsTeam lowestPlayers = BedwarsTeam.values()[0];
            for (BedwarsTeam team : BedwarsTeam.values()) {
                if (team.equals(BedwarsTeam.SPECTATOR)) continue;
                if ((teamSize - getTeamMembers(team).size()) > (teamSize - getTeamMembers(lowestPlayers).size())) lowestPlayers = team;
            }

            int spare = teamSize - getTeamMembers(lowestPlayers).size();
            for (UUID member : party.getMembers()) {
                if (!players.containsKey(member)) continue;
                if (spare > 0) {
                    getTeamMembers(lowestPlayers).add(member);
                    spare--;
                } else {
                    getTeamMembers(BedwarsTeam.SPECTATOR).add(member);
                }
            }
        }

        // Calculate Solo Teams
        for (UUID player : players.keySet()) {
            if (parties.stream().anyMatch(gameParty -> gameParty.getMembers().contains(player))) continue;
            BedwarsTeam lowestPlayers = BedwarsTeam.values()[0];
            for (BedwarsTeam team : BedwarsTeam.values()) {
                if (team.equals(BedwarsTeam.SPECTATOR)) continue;
                if ((teamSize - getTeamMembers(team).size()) > (teamSize - getTeamMembers(lowestPlayers).size())) lowestPlayers = team;
            }

            if (teamSize - getTeamMembers(lowestPlayers).size() > 0) getTeamMembers(lowestPlayers).add(player);
            else getTeamMembers(BedwarsTeam.SPECTATOR).add(player);
        }

        // Display Teams
        for (BedwarsTeam team : BedwarsTeam.values()) {
            System.out.println(team + ": " + getTeamMembers(team));
        }

        // Teleport Players && Set PlayerState
        for (UUID player : players.keySet()) {
            if (getPlayerTeam(player).equals(BedwarsTeam.SPECTATOR)) {
                PlayerUtils.prepare(Bukkit.getPlayer(player), GameMode.SPECTATOR, new ItemStack[]{}, new ItemStack[]{}, PlayerState.SPECTATOR);
            }
            else {
                PlayerUtils.prepare(Bukkit.getPlayer(player), GameMode.SURVIVAL, new ItemStack[]{}, new ItemStack[]{}, PlayerState.ALIVE);
            }
        }
    }

    public void handleLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (getState().equals(GameState.WAITING)) {
            PlayerUtils.prepare(player, GameMode.ADVENTURE, new ItemStack[]{}, new ItemStack[]{}, PlayerState.WAITING);
            event.setJoinMessage(null);

            if (players.keySet().size() >= required) startCountdown();
        } else if (getState().equals(GameState.PLAYING)) {
            if (getPlayerState(player.getUniqueId()).equals(PlayerState.ALIVE)) {
                PlayerUtils.prepare(player, GameMode.SURVIVAL, new ItemStack[]{}, new ItemStack[]{}, PlayerState.ALIVE);
                respawn(player);

                event.setJoinMessage(null);
            } else {
                PlayerUtils.prepare(player, GameMode.SPECTATOR, new ItemStack[]{}, new ItemStack[]{}, PlayerState.SPECTATOR);
            }
        } else {
            PlayerUtils.prepare(player, GameMode.SPECTATOR, new ItemStack[]{}, new ItemStack[]{}, PlayerState.SPECTATOR);
        }
    }

    public void handleLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (getState().equals(GameState.WAITING)) {
            players.remove(player.getUniqueId());
            if (players.keySet().size() < required) cancelCountdown();
        } else if (getState().equals(GameState.PLAYING) && getPlayerState(player.getUniqueId()).equals(PlayerState.ALIVE)) {
            // TODO Disconnect Player
        }
    }

    private void respawn(Player player) {
        // TODO Respawn
    }

    public void handleBlockBreak(BlockBreakEvent event) {
        event.setCancelled(!placedBlocks.contains(event.getBlock().getLocation()));
    }

    public void handleBlockPlace(BlockPlaceEvent event) {
        if (!placedBlocks.contains(event.getBlock().getLocation())) placedBlocks.add(event.getBlock().getLocation());
    }

    public GameState getState() {
        return state;
    }

    public PlayerState getPlayerState(UUID player) {
        return players.get(player);
    }

    public void setPlayerState(UUID player, PlayerState state) {
        players.put(player, state);
    }

    public BedwarsTeam getPlayerTeam(UUID player) {
        if (teams.keySet().stream().noneMatch(team -> getTeamMembers(team).contains(player))) return BedwarsTeam.SPECTATOR;
        else return teams.keySet().stream().filter(team -> getTeamMembers(team).contains(player)).findAny().get();
    }

    public List<UUID> getTeamMembers(BedwarsTeam team) {
        return teams.get(team);
    }

}
