package net.mcnations.bb.game.states;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.BuildObjects;
import net.mcnations.bb.game.GameUI;
import net.mcnations.bb.utils.SoundHelper;
import net.mcnations.bb.utils.player.BlockListener;
import net.mcnations.bb.utils.player.RandomTeleport;
import net.mcnations.bb.utils.team.TeamHelper;
import net.mcnations.bb.world.PlotHelper;
import net.mcnations.bb.world.WorldHelper;
import net.mcnations.core.common.general.cache.MCNPlayerCache;
import net.mcnations.core.common.general.gameplayers.MCNPlayer;
import net.mcnations.core.common.utils.PlayerUtils;
import net.mcnations.core.common.utils.RewardUtils;
import net.mcnations.core.engine.GameEngine;
import net.mcnations.core.engine.GameState;

public class InJudge extends GameState {

	public static int judgeTime = 15;
	public static int currentPlot = 0;
	private static int plotScoreRating = 0;
	public static boolean judgingOver = false;
	public static List<Integer> plotRating = new ArrayList<Integer>();

	// This is for fireworks, remove lated

	public InJudge(GameEngine engine, String rawName, String displayName) {
		super(engine, rawName, displayName);

	}

	@Override
	public boolean onStateBegin() {

		if (BlockListener.noOneBuilt()) {
			BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(0));
			Bukkit.broadcastMessage(ChatColor.RED + "No one built anything!");
			return false;
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.getInventory().clear();
			player.setGameMode(GameMode.ADVENTURE);
			player.setAllowFlight(true);
			player.setFlying(true);
			GameUI.giveJudgeItems(player);

		}
		return true;
	}

	@Override
	public boolean onStateEnd() {

		if (!winningPlotTie()) {
			for (Player player : Bukkit.getOnlinePlayers()) {

				if (isPlayerWinner(player))
					RewardUtils.addXP(player, BuildBattle.getCorePlugin().getConfig().getInt("Game.DefaultXP") + 10,
							true);
				else
					RewardUtils.addXP(player, BuildBattle.getCorePlugin().getConfig().getInt("Game.DefaultXP"), true);

				RewardUtils.announceWinings(player, ChatColor.translateAlternateColorCodes('&', "&e&lBlock Builders"),
						ChatColor.GREEN + PlotHelper.getPlayerPlotName(winningPlot()).getName()
								+ ChatColor.translateAlternateColorCodes('&', " &ahas won the game!"),
						"", ChatColor.translateAlternateColorCodes('&',
								"&6You gained " + RewardUtils.getGainedXP(player) + " xp this game!"));
			}

			RewardUtils.randomiseRewards();
			RewardUtils.clearGainedXP();
		}

		RandomTeleport.savedLocations.clear();

		return true;
	}

	@Override
	public void tick() {
		WorldHelper.setAlwaysDay(WorldHelper.getPlayWorld());

		if (currentPlot >= TeamHelper.oldPlayerSize()) {
			judgingOver = true;
			// fixed
			for (Player player : Bukkit.getOnlinePlayers())
				player.getInventory().clear();

			if (!winningPlotTie() || BlockListener.onlyOneBuilt()) {
				// THIS IS FOR 1 WINNING PLOT ONLY!!!!!!!!

				PlotHelper.displayPlotScoreboard();

				currentPlot = -1;

				nextPlot(currentPlot, true);

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BuildBattle.getCorePlugin(), new Runnable() {
					public void run() {
						BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(0));
					}

				}, 20 * 15);

			} else {

				currentPlot = -1;

				final List<Integer> winningPlots = new ArrayList<Integer>();

				for (int i = 0; i < InJudge.plotRating.size(); i++) {
					if (InJudge.plotRating.get(i) == Collections.max(InJudge.plotRating))
						winningPlots.add(i);
				}

				for (Player player : Bukkit.getOnlinePlayers()) {

					GameUI.resetPointsGiven(player);
					GameUI.giveJudgeItems(player);
					MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
					corePlayer.sendTitleBar(0, 20, 0, ChatColor.translateAlternateColorCodes('&', "&6Tie!"),
							ChatColor.translateAlternateColorCodes('&',
									"&6Players Tied: &e" + InJudge.getPoint(winningPlots, 0)
											+ InJudge.getPoint(winningPlots, 1) + InJudge.getPoint(winningPlots, 2)
											+ InJudge.getPoint(winningPlots, 3) + InJudge.getPoint(winningPlots, 4)));
				}

				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BuildBattle.getCorePlugin(), new Runnable() {
					public void run() {
						InTiebreaker.initializeWinningPlots(winningPlots);
						BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(3));
					}

				}, 20 * 3);

			}

			return;

		}

		if (!judgingOver) {
			// SKIPS PLOT
			/*
			 * if (!PlotHelper.getPlayerPlotName(currentPlot).isOnline() ||
			 * !BlockListener.requiredBlocks(PlotHelper.getPlayerPlotName(
			 * currentPlot))) { skipPlot = true; Bukkit.broadcastMessage(
			 * "Should be skipping"); nextPlot(currentPlot, false); return; }
			 */

			if (currentPlot == 0 && judgeTime == 15) {

				if (!PlotHelper.getPlayerPlotName(currentPlot).isOnline()
						|| !BlockListener.requiredBlocks(PlotHelper.getPlayerPlotName(currentPlot)))
					judgeTime = -2;

				nextPlot(currentPlot, false);
			}

			if (judgeTime < 4 && judgeTime != -1 && judgeTime != -2) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
					corePlayer.sendTitleBar(0, 0, 0, ChatColor.translateAlternateColorCodes('&', "&e" + judgeTime), "");
					if (judgeTime > 0)
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 5F, 1F);
					else
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 5F, 10F);
				}
			}

			judgeTime--;

			if (judgeTime < 0) {

				// This should be up here
				judgeTime = defaultJudgeTime();

				// This if statement add's the score to the plot that is online!
				if (PlotHelper.getPlayerPlotName(currentPlot).isOnline()
						&& BlockListener.requiredBlocks(PlotHelper.getPlayerPlotName(currentPlot))) {
					plotRating.add(currentPlot, getPlotPoints());

					if (isPlotEpic(currentPlot))
						Bukkit.broadcastMessage(ChatColor.GOLD + PlotHelper.getPlayerPlotName(currentPlot).getName()
								+ "'s plot had an " + ChatColor.DARK_PURPLE + "EPIC" + ChatColor.GOLD + " score!");
				}

				currentPlot++;

				// This if statement checks if were not teleporting to a weird
				// plot
				if (currentPlot != TeamHelper.oldPlayerSize() && currentPlot != -1) {

					// CHECKING TO SKiP PLOT
					if (!PlotHelper.getPlayerPlotName(currentPlot).isOnline()
							|| !BlockListener.requiredBlocks(PlotHelper.getPlayerPlotName(currentPlot)))
						judgeTime = -2;

					nextPlot(currentPlot, false);
				}

			}

		}

		for (Player player : Bukkit.getOnlinePlayers())
			sendJudgeScoreboard(player);

	}

	public static int defaultJudgeTime() {
		return 15;
	}

	public static int getPlotPoints() {
		plotScoreRating = 0;
		for (Player player : Bukkit.getOnlinePlayers()) {
			String name = ChatColor.stripColor(player.getInventory().getItem(9).getItemMeta().getDisplayName());

			if (player.getInventory().contains(Material.EMERALD)
					&& (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE")
							|| BuildBattle.getGame().getCurrentState().getRawName().equals("IN_TIEBREAKER"))
					&& name.matches(".*\\d+.*")) {
				plotScoreRating += Integer.parseInt(name.replaceAll("\\D+", ""));
			}
		}

		return plotScoreRating;

	}

	public static int winningPlot() {
		if(BlockListener.didPlayerBuild(PlotHelper.getPlayerPlotName(plotRating.indexOf(Collections.max(plotRating)))))
		return plotRating.indexOf(Collections.max(plotRating));
		else
		{
			plotRating.remove(Collections.max(plotRating));
			winningPlot();
		}
		
		return -69;
	}

	public static boolean winningPlotTie() {
		try {
			if (countNumberEqual(plotRating, Collections.max(plotRating)) > 1)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static int countNumberEqual(List<Integer> itemList, Integer itemToCheck) {
		int count = 0;
		for (Integer i : itemList) {
			if (i.equals(itemToCheck)) {
				count++;
			}
		}
		return count;
	}

	public static void sendJudgeScoreboard(Player player) {

		MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
		PlayerUtils.sendScoreboard(player,
				new String[] { ChatColor.translateAlternateColorCodes('&', "&e&lBLOCK BUILDERS"), ChatColor.GOLD + " ",
						// we check for actual players
						ChatColor.translateAlternateColorCodes('&', "&6Builder: &f&l" + (getPlotOwner())),
						ChatColor.AQUA + "  ",
						ChatColor.translateAlternateColorCodes('&', "&6Build: &f&l" + BuildObjects.getRandomBuild()),
						ChatColor.GRAY + "      ",
						ChatColor.translateAlternateColorCodes('&',
								"&6Vote: &f&l" + translatePlotVote(GameUI.getPlayerItemPoints(player))),
				ChatColor.BLACK + "   ",
				ChatColor.translateAlternateColorCodes('&', "&6Time Left: &f&l" + (judgeTime + 1)),
				ChatColor.DARK_AQUA + "       ", " ", ChatColor.translateAlternateColorCodes('&', "&fMCNations.net") });
	}

	public static String getPlotOwner() {

		try {

			if (currentPlot != -1)
				return PlotHelper.getPlayerPlotName(currentPlot).getName();
			else
				return PlotHelper.getPlayerPlotName(winningPlot()).getName();

		} catch (IndexOutOfBoundsException e) {
			return "Loading...";
		}

	}

	public static int getPlot(int i) {
		if (i == -1)
			return winningPlot();

		return currentPlot;
	}

	static String translatePlotVote(int i) {
		switch (i) {
		case 0:
			return "Poop";
		case 1:
			return "Meh...";
		case 2:
			return "Ok...";
		case 3:
			return "Good";
		case 4:
			return "Amazing";
		case 5:
			return "Epic";
		}

		return "Loading...";
	}

	public static void nextPlot(int plot, boolean winningPlot) {
		if (currentPlot < TeamHelper.oldPlayerSize())
			for (Player player : Bukkit.getOnlinePlayers()) {
				MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());

				if (currentPlot != -1) {
					boolean skipPlot = (!PlotHelper.getPlayerPlotName(currentPlot).isOnline()
							|| !BlockListener.requiredBlocks(PlotHelper.getPlayerPlotName(currentPlot)));

					if (skipPlot) {
						plotRating.add(currentPlot, 0);
						// currentPlot++;
						judgeTime = -1;
						GameUI.resetPointsGiven(player);
						return;
					}

				}

				if (!winningPlot) {

					player.setPlayerTime(PlotHelper.getPlotTime(plot), true);
					WorldHelper.setPlayerWeather(player, PlotHelper.getPlotWeather(plot));

					RandomTeleport.teleport(player, PlotHelper.getPlot(plot));

					// player.teleport(PlotHelper.getPlot(currentPlot));
					corePlayer.sendTitleBar(0, 0, 0,
							ChatColor.translateAlternateColorCodes('&',
									"&6" + PlotHelper.getPlayerPlotName(plot).getName() + "'s Plot"),
							ChatColor.translateAlternateColorCodes('&', "&6Build: &e" + BuildObjects.getRandomBuild()));
					GameUI.resetPointsGiven(player);

				} else if (winningPlot) {
					player.teleport(PlotHelper.getPlot(winningPlot()));

					player.setPlayerTime(PlotHelper.getPlotTime(winningPlot()), true);
					WorldHelper.setPlayerWeather(player, PlotHelper.getPlotWeather(winningPlot()));

					corePlayer.sendTitleBar(0, 20, 0,
							ChatColor.translateAlternateColorCodes('&',
									"&6" + PlotHelper.getPlayerPlotName(winningPlot()).getName() + " has won!"),
							ChatColor.translateAlternateColorCodes('&',
									"&6Points Received: &r" + plotRating.get(winningPlot())));
				}
			}

	}

	public static boolean isPlotEpic(int currentPlot) {
		int rating = plotRating.get(currentPlot);
		int playerSize = Bukkit.getOnlinePlayers().size();

		if ((5 * playerSize) - 3 == rating) {
			return true;
		}

		return false;

	}
	
	public static boolean isPlayerWinner(Player player) {
		if (player.getName() == getPlotOwner())
			return true;

		return false;
	}

	static String getPoint(List<Integer> array, int i) {

		if (i > 4 && getPoint(array, i - 1) == "")
			return "And More...";

		if (i == array.size() - 1)
			return PlotHelper.getPlayerPlotName(array.get(i)).getName();

		if (i < array.size())
			return PlotHelper.getPlayerPlotName(array.get(i)).getName() + ", ";

		return "";
	}

}
