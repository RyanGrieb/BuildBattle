package net.mcnations.bb.utils;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.states.InJudge;
import net.mcnations.bb.game.states.InTiebreaker;
import net.mcnations.bb.utils.particles.FireworkHelper;
import net.mcnations.bb.world.PlotHelper;
import net.mcnations.bb.world.WorldHelper;
import net.mcnations.core.common.general.cache.MCNPlayerCache;
import net.mcnations.core.common.general.gameplayers.MCNPlayer;

public class TimerHelper {
	public static void addBossBar() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BuildBattle.getCorePlugin(), new Runnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
					corePlayer.sendBossBar(
							ChatColor.translateAlternateColorCodes('&', "&e&lBlock Builders by &6MCNations"));
				}
			}
		}, 0, 20);
	}

	public static void judgeItemsActionBar() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BuildBattle.getCorePlugin(), new Runnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
					if (!player.getInventory().getItemInHand().equals(Material.AIR)
							&& BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE"))

						if (player.getInventory().getItemInHand().getType() == Material.STAINED_CLAY
								&& player.getInventory().getHeldItemSlot() != 8)
							corePlayer.sendActionBar(
									player.getInventory().getItemInHand().getItemMeta().getDisplayName());
						else
							corePlayer.sendActionBar("");
				}
			}
		}, 0, 2);
	}

	public static void fireworkTimer() {

		new BukkitRunnable() {
			@Override
			public void run() {

				Color color = FireworkHelper.getRandomColor(17);

				if ((InJudge.currentPlot == -1 && !InJudge.winningPlotTie()
						&& !BuildBattle.getGame().getCurrentState().getRawName().equals("IN_TIEBREAKER"))
						|| (InTiebreaker.tiebreakerOver)) {
					int x = (int) PlotHelper.getPlot(InJudge.winningPlot()).getX();
					int y = (int) PlotHelper.getPlot(InJudge.winningPlot()).getY() + 1;
					int z = (int) PlotHelper.getPlot(InJudge.winningPlot()).getZ();

					x = x + RandomNumber.getRandomNumber(7);
					z = z + RandomNumber.getRandomNumber(7);
					FireworkHelper.spawnFirework(new Location(WorldHelper.getPlayWorld(), x, y + 10, z),
							org.bukkit.FireworkEffect.Type.BALL_LARGE, color);
				}
			}
		}.runTaskTimer(BuildBattle.getCorePlugin(), 0L, 10L);
	}

}
