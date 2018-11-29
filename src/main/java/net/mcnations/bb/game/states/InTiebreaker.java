package net.mcnations.bb.game.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.BuildObjects;
import net.mcnations.bb.game.GameUI;
import net.mcnations.bb.world.PlotHelper;
import net.mcnations.core.common.general.cache.MCNPlayerCache;
import net.mcnations.core.common.general.gameplayers.MCNPlayer;
import net.mcnations.core.common.utils.PlayerUtils;
import net.mcnations.core.common.utils.RewardUtils;
import net.mcnations.core.engine.GameEngine;
import net.mcnations.core.engine.GameState;

public class InTiebreaker extends GameState {

	private static int judgeTime = 0;

	public static List<Integer> winningPlots = new ArrayList<Integer>();
	public static int currentPlot = -1;
	public static boolean tiebreakerOver = false;

	public InTiebreaker(GameEngine engine, String rawName, String displayName) {
		super(engine, rawName, displayName);
	}

	@Override
	public boolean onStateBegin() {
		//Collections.fill(InJudge.plotRating, 0);
		tiebreakerOver = false;
		return true;
	}

	@Override
	public boolean onStateEnd() {

		for (Player player : Bukkit.getOnlinePlayers()) {

			if (InJudge.isPlayerWinner(player))
				RewardUtils.addXP(player, BuildBattle.getCorePlugin().getConfig().getInt("Game.DefaultXP") + 10, true);
			else
				RewardUtils.addXP(player, BuildBattle.getCorePlugin().getConfig().getInt("Game.DefaultXP"), true);

			RewardUtils.announceWinings(player, ChatColor.translateAlternateColorCodes('&', "&e&lBlock Builders"),
					ChatColor.GREEN + PlotHelper.getPlayerPlotName(InJudge.winningPlot()).getName()
							+ ChatColor.translateAlternateColorCodes('&', " &ahas won the game!"),
					"", ChatColor.translateAlternateColorCodes('&',
							"&6You gained " + RewardUtils.getGainedXP(player) + " xp this game!"));
		}

		RewardUtils.randomiseRewards();
		RewardUtils.clearGainedXP();

		judgeTime = 0;
		winningPlots.clear();
		currentPlot = -1;
		tiebreakerOver = false;
		return true;
	}

	@Override
	public void tick() {

		// When state ends
		if (!tiebreakerOver) {
			for (Player player : Bukkit.getOnlinePlayers())
				sendJudgeScoreboard(player);

			if (currentPlot >= winningPlots.size()) {

				for (Player player : Bukkit.getOnlinePlayers())
					player.getInventory().clear();

				judgeTime = InJudge.defaultJudgeTime();

				tiebreakerOver = true;

				InJudge.nextPlot(currentPlot, true);
				
				PlotHelper.displayPlotScoreboard();
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BuildBattle.getCorePlugin(), new Runnable() {
					public void run() {
						BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(0));
						return;
					}
				}, 20 * 15);

				return;
			}

			// For titlebar countdown, ignore
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
			// THIS IS THE BUGGY METHOD, it is called before the one above ???
			if (judgeTime < 0 && !tiebreakerOver) {
				try {
					if (currentPlot != -1)
						InJudge.plotRating.add(winningPlots.get(currentPlot), InJudge.getPlotPoints());
					currentPlot++;
					
					if(currentPlot != winningPlots.size())
					InJudge.nextPlot(winningPlots.get(currentPlot), false);
					
					judgeTime = InJudge.defaultJudgeTime();
				} catch (Exception e) {
				}
			}
		}
	}

	public static void sendJudgeScoreboard(Player player) {

		MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
		PlayerUtils.sendScoreboard(player,
				new String[] { ChatColor.translateAlternateColorCodes('&', "&e&lBLOCK BUILDERS"), ChatColor.GOLD + " ",
						// we check for actual players
						ChatColor.translateAlternateColorCodes('&', "&6Builder: &f&l" + getPlotOwner()),
						ChatColor.AQUA + "  ",
						ChatColor.translateAlternateColorCodes('&', "&6Build: &f&l" + BuildObjects.getRandomBuild()),
						ChatColor.GRAY + "      ",
						ChatColor.translateAlternateColorCodes('&',
								"&6Vote: &f&l" + InJudge.translatePlotVote(GameUI.getPlayerItemPoints(player))),
				ChatColor.BLACK + "   ", ChatColor.translateAlternateColorCodes('&', "&6Time Left: &f&l" + (judgeTime)),
				ChatColor.DARK_AQUA + "       ", " ", ChatColor.translateAlternateColorCodes('&', "&fMCNations.net") });
	}

	public static void initializeWinningPlots(List<Integer> plots) {
		winningPlots = plots;
	}

	public static String getPlotOwner() {

		try {

			if (currentPlot == winningPlots.size())
				return PlotHelper.getPlayerPlotName(InJudge.winningPlot()).getName();
			else
				return PlotHelper.getPlayerPlotName(winningPlots.get(currentPlot)).getName();

		} catch (IndexOutOfBoundsException e) {
			return "Loading...";
		}

	}
	
	public static int getTimeLeft()
	{
		if(judgeTime == -1)
			return 0;
		return judgeTime;
	}

}
