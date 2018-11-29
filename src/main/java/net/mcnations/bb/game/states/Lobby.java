package net.mcnations.bb.game.states;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.BuildObjects;
import net.mcnations.bb.game.Game;
import net.mcnations.bb.game.GameUI;
import net.mcnations.bb.utils.player.BlockListener;
import net.mcnations.bb.utils.team.TeamHelper;
import net.mcnations.bb.world.PlotHelper;
import net.mcnations.bb.world.WorldHelper;
import net.mcnations.core.common.general.cache.MCNPlayerCache;
import net.mcnations.core.common.general.gameplayers.MCNPlayer;
import net.mcnations.core.common.utils.PlayerUtils;
import net.mcnations.core.engine.GameEngine;
import net.mcnations.core.engine.GameState;

public class Lobby extends GameState {

	public static int lobbyTimeLeft = defaultLobbyCooldownTime();

	public Lobby(GameEngine engine, String rawName, String displayName) {
		super(engine, rawName, displayName);
	}

	@Override
	public boolean onStateBegin() {

		BlockListener.placedBlocks.clear();

		for (Player player : Bukkit.getOnlinePlayers()) {

			player.setLevel(0);
			player.setExp(0);

			WorldHelper.resetPlotSettings(player);
			player.setGameMode(GameMode.ADVENTURE);
			player.teleport(WorldHelper.getHubLocation());
			player.getInventory().clear();
			player.setAllowFlight(false);
			InJudge.judgeTime = InJudge.defaultJudgeTime();
			InJudge.currentPlot = 0;
			InJudge.plotRating.clear();
			// It's not over b/c the next game will need to judge too.
			InJudge.judgingOver = false;
			PlotHelper.clearFloorSettings();
			player.setFoodLevel(20);
			TeamHelper.clearOldPlayer();

			GameUI.giveHubItems(player);

			WorldHelper.deleteWorld(false);
			new BukkitRunnable() {
				public void run() {
					WorldHelper.loadWorld();
				}
			}.runTaskLater(BuildBattle.getCorePlugin(), 2 * 20L);
		}

		return true;
	}

	@Override
	public boolean onStateEnd() {
		return true;
	}

	@Override
	public void tick() {

		WorldHelper.setAlwaysDay(WorldHelper.getHubWorld());

		InGame.buildTimeLeft = InGame.defaultBuildTime();
		// we check for actual players
		if (TeamHelper.buildPlayerSize() >= BuildBattle.getCorePlugin().getConfig().getInt("MaxPlayersReady")
				&& BuildBattle.getGame().getCurrentState().getRawName().equals("IN_LOBBY")) {

			lobbyTimeLeft--;

			for (Player player : Bukkit.getOnlinePlayers()) {
				MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
				corePlayer.sendActionBar(
						ChatColor.translateAlternateColorCodes('&', "&6Starting game in: &r&l") + lobbyTimeLeft);
			}

			if (lobbyTimeLeft <= 0) {

				BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(1));
				lobbyTimeLeft = defaultLobbyCooldownTime();
			}

			// we check for real players
		} else if (TeamHelper.buildPlayerSize() < BuildBattle.getCorePlugin().getConfig().getInt("MaxPlayersReady"))
			lobbyTimeLeft = defaultLobbyCooldownTime();

		for (Player player : Bukkit.getOnlinePlayers()) {
			sendHubScoreboard(player);
		}

	}

	public static void sendHubScoreboard(Player player) {

		MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
		PlayerUtils.sendScoreboard(player,
				new String[] { ChatColor.translateAlternateColorCodes('&', "&e&lBLOCK BUILDERS"), ChatColor.GOLD + " ",
						// we check for actual players
						ChatColor.translateAlternateColorCodes('&',
								"&6Players: &f&l" + ((int) TeamHelper.buildPlayerSize()) + "/"
										+ BuildBattle.getCorePlugin().getConfig().getInt("MaxPlayers")),
				ChatColor.AQUA + "  ",
				ChatColor.translateAlternateColorCodes('&', "&6Time Left: &f&l" + (Lobby.lobbyTimeLeft - 1)),
				ChatColor.GRAY + "      ",
				ChatColor.translateAlternateColorCodes('&',
						"&6Player XP: &r&l" + Game.formatInteger(corePlayer.getXP())),
				ChatColor.WHITE + "    ",
				ChatColor.translateAlternateColorCodes('&',
						"&6Nation Coins: &f&l" + Game.formatInteger(corePlayer.getNationCoins())), " ",
				ChatColor.DARK_AQUA + "       ", ChatColor.translateAlternateColorCodes('&', "&fMCNations.net") });
	}

	private static int defaultLobbyCooldownTime() {
		return BuildBattle.getCorePlugin().getConfig().getInt("LobbyCooldown");
	}

}