package dev.dynxmic.dynamicbedwars.game;

import dev.dynxmic.dynamicbedwars.util.ChatUtils;
import dev.dynxmic.dynamicbedwars.util.PlayerUtils;
import dev.dynxmic.dynamicbedwars.util.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
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
    private final int maximum;

    private final Map<UUID, PlayerState> players;
    private final Map<BedwarsTeam, List<UUID>> teams;
    private final Map<BedwarsTeam, Boolean> beds;
    private final List<GameParty> parties;
    private final List<Location> placedBlocks;

    public GameHandler(int teamSize, int required, int maximum) {
        this.players = new HashMap<>();
        this.teams = new HashMap<>();
        this.beds = new HashMap<>();
        this.parties = new ArrayList<>();
        this.placedBlocks = new ArrayList<>();
        this.countdownID = 0;
        this.state = GameState.WAITING;
        this.teamSize = teamSize;
        this.required = required;
        this.maximum = maximum;

        // Blank List For Team Players
        for (BedwarsTeam team : BedwarsTeam.values()) teams.put(team, new ArrayList<>());
    }

    private void startCountdown() {
        // Return if Running Countdown
        if (countdownID != 0) return;

        // Run Countdown Task
        countdown = 20;
        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                if (countdown == 0) {
                    // Cancel Task And Start Game
                    cancel();
                    startGame();
                    return;
                }

                broadcastStart();
                countdown--;
            }

        }.runTaskTimer(PluginUtils.getPlugin(), 0L, 20L);  // Run Every Second
        countdownID = task.getTaskId();  // Set Countdown As The Running Countdown
    }

    public void cancelCountdown() {
        // Return If No Running Countdown
        if (countdownID == 0) return;

        // Cancel Countdown
        Bukkit.getScheduler().cancelTask(countdownID);
        ChatUtils.broadcast("&cWe don't have enough players! Start cancelled.");

        // Set No Running Countdown
        countdownID = 0;
    }

    private void broadcastStart() {
        // Yellow Message When 20 Seconds Until Start + Click Sound
        if (countdown == 20) {
            ChatUtils.broadcast("&eThe game starts in &e" + countdown + " &eseconds!");
            ChatUtils.broadcastSound(Sound.CLICK);
        }

        // Orange Message When 20 Seconds Until Start + Green Title + Click Sound
        if (countdown == 10) {
            ChatUtils.broadcast("&eThe game starts in &6" + countdown + " &eseconds!");
            ChatUtils.broadcastSound(Sound.CLICK);
            ChatUtils.broadcastTitle("&a" + countdown);
        }

        // Red Message When 3/2/1 Seconds Until Start + Red Title + Click Sound
        if (countdown < 4) {
            ChatUtils.broadcast("&eThe game starts in &c" + countdown + " &eseconds!");
            ChatUtils.broadcastSound(Sound.CLICK);
            ChatUtils.broadcastTitle("&c" + countdown);
        }

        // Red Message When 20 Seconds Until Start + Yellow Title + Click Sound
        else if (countdown < 6) {
            ChatUtils.broadcast("&eThe game starts in &c" + countdown + " &eseconds!");
            ChatUtils.broadcastSound(Sound.CLICK);
            ChatUtils.broadcastTitle("&e" + countdown);
        }
    }

    private void startGame() {
        // Set GameState Playing
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

        // Set Team Beds If Team Exists
        for (BedwarsTeam team : BedwarsTeam.values()) beds.put(team, getTeamMembers(team).size() > 0);
    }

    public void handleLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Set Player As Spectator If not Defined
        if (!players.containsKey(player.getUniqueId())) {
            setPlayerState(player.getUniqueId(), PlayerState.SPECTATOR);
        }

        if (getState().equals(GameState.WAITING)) {
            // Waiting Join
            PlayerUtils.prepare(player, GameMode.ADVENTURE, new ItemStack[]{}, new ItemStack[]{}, PlayerState.WAITING);
            ChatUtils.broadcast(player.getDisplayName() + " &ehas joined (&b" + players.size() + "&e/&b" + maximum + "&e)!");
            event.setJoinMessage(null);

            if (players.keySet().size() >= required) startCountdown();
        } else if (getState().equals(GameState.PLAYING) && getPlayerState(player.getUniqueId()).equals(PlayerState.ALIVE)) {
            // Player Reconnect
            PlayerUtils.prepare(player, GameMode.SURVIVAL, new ItemStack[]{}, new ItemStack[]{}, PlayerState.ALIVE);
            ChatUtils.broadcast(player.getDisplayName() + " &7reconnected");
        } else {
            // Spectator Reconnect
            PlayerUtils.prepare(player, GameMode.SPECTATOR, new ItemStack[]{}, new ItemStack[]{}, PlayerState.SPECTATOR);
        }

        event.setJoinMessage(null);
    }

    public void handleLogout(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (getState().equals(GameState.WAITING)) {
            // Waiting Quit
            players.remove(player.getUniqueId());
            ChatUtils.broadcast(player.getDisplayName() + " &ehas quit!");
            if (players.keySet().size() < required) cancelCountdown();
        } else if (getState().equals(GameState.PLAYING) && getPlayerState(player.getUniqueId()).equals(PlayerState.ALIVE)) {
            // Player Disconnect
            ChatUtils.broadcast(player.getDisplayName() + " &7disconnected");
        }

        event.setQuitMessage(null);
    }

    public void handleBlockBreak(BlockBreakEvent event) {
        // Cancel Event If Block Is Part Of Map
        event.setCancelled(!placedBlocks.contains(event.getBlock().getLocation()));
    }

    public void handleBlockPlace(BlockPlaceEvent event) {
        // Add Block To List Of Blocks Placed By Players
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
        // Return Spectator If Not Defined
        if (teams.keySet().stream().noneMatch(team -> getTeamMembers(team).contains(player))) return BedwarsTeam.SPECTATOR;
        else return teams.keySet().stream().filter(team -> getTeamMembers(team).contains(player)).findAny().get();
    }

    public List<UUID> getTeamMembers(BedwarsTeam team) {
        return teams.get(team);
    }

}
