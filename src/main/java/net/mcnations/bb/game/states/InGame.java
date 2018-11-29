package net.mcnations.bb.game.states;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.BuildObjects;
import net.mcnations.bb.game.GameUI;
import net.mcnations.bb.utils.SoundHelper;
import net.mcnations.bb.utils.team.TeamHelper;
import net.mcnations.bb.world.PlotHelper;
import net.mcnations.bb.world.WorldHelper;
import net.mcnations.core.common.general.cache.MCNPlayerCache;
import net.mcnations.core.common.general.gameplayers.MCNPlayer;
import net.mcnations.core.common.utils.PlayerUtils;
import net.mcnations.core.engine.GameEngine;
import net.mcnations.core.engine.GameState;

public class InGame extends GameState {

	public static int buildTimeLeft = defaultBuildTime();

	public InGame(GameEngine engine, String rawName, String displayName) {
		super(engine, rawName, displayName);
	}

	@Override
	public boolean onStateBegin() {

		BuildObjects.setRandomBuild();

		for (Player player : Bukkit.getOnlinePlayers()) {
			MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
			WorldHelper.resetPlotSettings(player);
			TeamHelper.addOldPlayer(player);
			player.setGameMode(GameMode.CREATIVE);
			PlotHelper.teleportPlayerToPlot(player);
			GameUI.giveGameItems(player);

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BuildBattle.getCorePlugin(), new Runnable() {
				public void run() {
					corePlayer.sendTitleBar(0, 20, 0,
							ChatColor.translateAlternateColorCodes('&', "&6" + BuildObjects.getRandomBuild() + "!"),
							ChatColor.translateAlternateColorCodes('&', "&eComplete the build within the given time"));// ChatColor.translateAlternateColorCodes('&',
																														// "&6Build:
																														// &e"
																														// +
																														// BuildObjects.getRandomBuild()));
					// TitleManager.sendTitle(player,
					// ChatColor.translateAlternateColorCodes('&', "&6Game
					// Starting..."),
					// ChatColor.translateAlternateColorCodes('&',"&6Build:
					// &e"+BuildObjects.getRandomBuild()),0,20,0);

					// SoundHelper.playRecord(player, player.getLocation(),
					// Material.RECORD_4);
				}
			}, 20 * 1);
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BuildBattle.getCorePlugin(), new Runnable() {
			public void run() {
				SoundHelper.setMusic();
			}

		}, 20 * 2);

		return true;
	}

	@Override
	public boolean onStateEnd() {
		BuildObjects.clearCategoryVotes();
		return true;
	}

	@Override
	public void tick() {
		WorldHelper.setAlwaysDay(WorldHelper.getPlayWorld());

		buildTimeLeft--;

		//Countdown timer
		if ((buildTimeLeft <= 10 || buildTimeLeft == 30 || buildTimeLeft == 60 || buildTimeLeft == 180) && buildTimeLeft > 0) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
				corePlayer.sendTitleBar(10, 0, 10,
						ChatColor.translateAlternateColorCodes('&',
								"&6" + translateTime(buildTimeLeft) + " " + getTimeName(buildTimeLeft)),
						ChatColor.YELLOW + "Hurry before time runs out!");
				
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 5F, 1F);
			}
		}

		if (buildTimeLeft < 0 && buildTimeLeft > -2) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
				corePlayer.sendTitleBar(0, 0, 0, ChatColor.translateAlternateColorCodes('&', "&cJudging Phase!"), "");

				player.playSound(player.getLocation(), Sound.LEVEL_UP, 5F, 1F);

				// This is here so no one bitches about being able
				// to build when judging has started.
				player.getInventory().clear();
				player.setGameMode(GameMode.ADVENTURE);
				player.setAllowFlight(true);
				player.setFlying(true);
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BuildBattle.getCorePlugin(), new Runnable() {
				public void run() {
					BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(2));
				}

			}, 20 * 3);

		}

		for (Player player : Bukkit.getOnlinePlayers()) {

			sendGameScoreboard(player);

		}

		if (TeamHelper.buildPlayerSize() <= 1) {
			BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(0));
			Bukkit.broadcastMessage(ChatColor.RED + "You're the only player left ):");
		}
	}


	public static void sendGameScoreboard(Player player) {
		MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
		PlayerUtils.sendScoreboard(player,
				new String[] { ChatColor.translateAlternateColorCodes('&', "&e&lBLOCK BUILDERS"), ChatColor.GOLD + " ",
						ChatColor.translateAlternateColorCodes('&', "&6Category: &f&l" + BuildObjects.votedCategory),
						ChatColor.GREEN + "  ",
						ChatColor.translateAlternateColorCodes('&',
								"&6Your Build: &f&l" + BuildObjects.getRandomBuild()),
				ChatColor.BLACK + "   ",
				ChatColor.translateAlternateColorCodes('&',
						"&6Players: &f&l" + ((int) TeamHelper.buildPlayerSize()) + "/"
								+ BuildBattle.getCorePlugin().getConfig().getInt("MaxPlayers")),
				ChatColor.GRAY + "      ",
				ChatColor.translateAlternateColorCodes('&', "&eTime Left: &f&l" + InGame.getGameTime()),
				ChatColor.WHITE + "    ", " ", ChatColor.translateAlternateColorCodes('&', "&fMCNations.net") });
	}

	public static String getGameTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.UK);

		Date date = new Date(buildTimeLeft * 1000);
		return formatter.format(date);
	}

	public static int defaultBuildTime() {
		return BuildBattle.getCorePlugin().getConfig().getInt("Game.BuildTime");
	}

	private static int defaultLobbyCooldownTime() {
		return BuildBattle.getCorePlugin().getConfig().getInt("LobbyCooldown");
	}

	private static String getTimeName(int i) {
		
		if(i == 60)
			return "Minute";
		
		if (i > 60)
			return "Minutes!";
		
		if(i == 1)
			return "Second!";
		
		return "Seconds!";
	}
	

	private int translateTime(int sec) {
		if(sec >= 60)
			return sec/60;
		
		return sec;
	}

}
