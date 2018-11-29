package net.mcnations.bb.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.BuildObjects;
import net.mcnations.bb.game.GameUI;
import net.mcnations.bb.game.states.InJudge;
import net.mcnations.bb.utils.team.TeamHelper;
import net.mcnations.core.common.general.cache.MCNPlayerCache;
import net.mcnations.core.common.general.gameplayers.MCNPlayer;

public class PlotHelper {

	// Talk to beasty about puting these in a hash map
	private static List<Location> plotnum = new ArrayList<Location>();
	private static List<ItemStack> plotFloor = new ArrayList<ItemStack>();
	private static List<Integer> plotTime = new ArrayList<Integer>();
	private static List<String> plotWeather = new ArrayList<String>();
	// private static List<String> plotBiome = new ArrayList<String>();
	// 6,000 is mid day
	// public static HashMap<Weather, Biome> plotWeather = new HashMap();

	public static void teleportPlayerToPlot(Player player) {
		plotnum.clear();
		initilizePlots();
		player.teleport(getPlayerPlot(player));
	}

	public static int getPlotAmount() {
		return plotnum.size();
	}

	/*
	 * private static void initilizeBorders() { for(int i = 0; i <
	 * plotnum.size(); i++) { Location center = plotnum.get(i); // delaying if
	 * statement here. Location min = new Location(center.getWorld(),
	 * center.getX() - 22, center.getY() - 1, center.getZ() - 22); Location max
	 * = new Location(center.getWorld(), center.getX() + 22, center.getY() - 1,
	 * center.getZ() + 22); for(int y = 0; y < 25; y++) for (int x = (int)
	 * min.getX(); x < (int) max.getX(); x++) { for (int z = (int) min.getZ(); z
	 * < (int) max.getZ(); z++) { barrierBorders.add(new
	 * Location(center.getWorld(), x, plotnum.get(i).getY()+y, z)); } } } }
	 */

	private static void initilizePlots() {

		int x = BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.x");
		int y = BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.y");
		int z = BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.z");
		// 33
		int spacing = BuildBattle.getCorePlugin().getConfig().getInt("PlotSpacing");
		// we check for fake players
		for (int i = 0; i < TeamHelper.oldPlayerSize(); i++) {
			plotFloor.add(i, new ItemStack(Material.STAINED_CLAY, 1, DyeColor.WHITE.getData()));
			plotTime.add(i, 6000);
			plotWeather.add("clear");

			if (i < 3)
				plotnum.add(i, WorldHelper.getCenterPlot().add((spacing * i), 0, 0));

			if (i >= 3 && i < 6)
				plotnum.add(i, WorldHelper.getCenterPlot().add((spacing * (i - 3)), 0, spacing));

			if (i >= 6 && i < 9)
				plotnum.add(i, WorldHelper.getCenterPlot().add((spacing * (i - 6)), 0, spacing * 2));
		}

	}

	public static Location getPlot(int i) {
		return plotnum.get(i);
	}

	public static Location getPlayerPlot(Player player) {
		// we check for fake players
		for (int i = 0; i < TeamHelper.oldPlayerSize(); i++) {
			if (TeamHelper.oldplayer.get(i) == player)
				return plotnum.get(i);
		}

		return null;
	}

	public static int getPlayerPlotNumber(Player player) {
		// we check for fake players
		for (int i = 0; i < TeamHelper.oldPlayerSize(); i++) {
			if (TeamHelper.oldplayer.get(i) == player)
				return i;
		}

		return -1;
	}

	public static Player getPlayerPlotName(int i) {
		return TeamHelper.oldplayer.get(i);
	}

	public static Integer getPlotTime(int i) {
		return plotTime.get(i);
	}

	public static String getPlotWeather(int i) {
		return plotWeather.get(i);
	}

	private static boolean isInBorder(Location location, Location loc1, Location loc2) {
		double[] dim = new double[2];

		dim[0] = loc1.getX();
		dim[1] = loc2.getX();
		Arrays.sort(dim);
		if (location.getX() > dim[1] || location.getX() < dim[0])
			return false;

		dim[0] = loc1.getZ();
		dim[1] = loc2.getZ();
		Arrays.sort(dim);
		if (location.getZ() > dim[1] || location.getZ() < dim[0])
			return false;

		dim[0] = loc1.getY();
		dim[1] = loc2.getY();
		Arrays.sort(dim);
		if (location.getY() > dim[1] || location.getY() < dim[0])
			return false;

		return true;
	}

	private static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
		List<Block> blocks = new ArrayList<Block>();

		int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

		int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Block block = loc1.getWorld().getBlockAt(x, y, z);

					blocks.add(block);
				}
			}
		}

		return blocks;
	}

	public static boolean isPlayerInBoder(Player player) {
		int plotNum = getPlayerPlotNumber(player);
		Location corner1 = new Location(player.getWorld(), getPlot(plotNum).getX() - 13, getPlot(plotNum).getY() - 3,
				getPlot(plotNum).getZ() - 13);
		Location corner2 = new Location(player.getWorld(), getPlot(plotNum).getX() + 13, getPlot(plotNum).getY() + 31,
				getPlot(plotNum).getZ() + 14);

		if (isInBorder(player.getLocation(), corner1, corner2)) {
			return true;
		}
		return false;
	}

	public static List<Block> getBlocksFromPlot(Player player) {
		int plotNum = getPlayerPlotNumber(player);

		Location corner1 = new Location(player.getWorld(), getPlot(plotNum).getX() - 13, getPlot(plotNum).getY() - 4,
				getPlot(plotNum).getZ() - 13);
		Location corner2 = new Location(player.getWorld(), getPlot(plotNum).getX() + 13, getPlot(plotNum).getY() + 31,
				getPlot(plotNum).getZ() + 13);

		return blocksFromTwoPoints(corner1, corner2);
	}

	public static boolean isBlockInBorder(Block block, Player player) {
		if (player.getWorld() == WorldHelper.getPlayWorld()) {
			int plotNum = getPlayerPlotNumber(player);
			Location corner1 = new Location(player.getWorld(), getPlot(plotNum).getX() - 13,
					// Plot Floor
					getPlot(plotNum).getY() - 1, getPlot(plotNum).getZ() - 13);
			Location corner2 = new Location(player.getWorld(), getPlot(plotNum).getX() + 13,
					getPlot(plotNum).getY() + 27, getPlot(plotNum).getZ() + 13);

			if (isInBorder(block.getLocation(), corner1, corner2)) {
				return true;
			}
			return false;
		}
		return true;
	}

	public static ItemStack getPlotFloor(int i) {
		return plotFloor.get(i);
	}

	public static void replaceFloor(ItemStack is, int i, int typeID) {
		plotFloor.set(i, is);

		if (is.getType() == Material.LAVA_BUCKET)
			is = new ItemStack(Material.LAVA);

		if (is.getType() == Material.WATER_BUCKET)
			is = new ItemStack(Material.WATER);

		Player player = PlotHelper.getPlayerPlotName(i);

		Location loc1 = new Location(player.getWorld(), getPlot(i).getX() - 13, getPlot(i).getY() - 1,
				getPlot(i).getZ() - 13);
		Location loc2 = new Location(player.getWorld(), getPlot(i).getX() + 13, getPlot(i).getY() - 1,
				getPlot(i).getZ() + 13);

		int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

		int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Block block = loc1.getWorld().getBlockAt(x, y, z);
					try {
						block.setType(is.getType());
						block.setData((byte) typeID);
					} catch (NullPointerException ex) {
						block.setType(Material.AIR);
					}
				}
			}
		}

	}

	public static void clearFloorSettings() {
		plotFloor.clear();
	}

	public static String convertPlotTime(int i) {
		switch (i) {
		case 0:
			return "6 am";

		case 3000:
			return "9 am";

		case 6000:
			return "12 pm";

		case 9000:
			return "3 pm";

		case 12000:
			return "6 pm";

		case 15000:
			return "9 pm";

		case 18000:
			return "12 am";

		case 21000:
			return "3 am";

		case 24000:
			return "6 am";
		}

		return "wtf";
	}

	public static int convertPlotTime(String str) {
		switch (str) {
		case "6 am":
			return 0;

		case "9 am":
			return 3000;

		case "12 pm":
			return 6000;

		case "3 pm":
			return 9000;

		case "6 pm":
			return 12000;

		case "9 pm":
			return 15000;

		case "12 am":
			return 18000;

		case "3 am":
			return 21000;

		}

		return -1;
	}

	public static void setPlotTime(int target, int i) {
		plotTime.set(target, i);
	}

	public static void resetTime() {
		plotTime.clear();
	}

	public static void setPlotWeather(int target, String str) {
		plotWeather.set(target, str);
	}

	public static void resetPlotWeather() {
		plotWeather.clear();
	}

	public static void setPlotBiome(Player player, String itemName) {
		List<Block> blocks = getBlocksFromPlot(player);
		int plotNum = getPlayerPlotNumber(player);
		Location corner1 = new Location(player.getWorld(), getPlot(plotNum).getX() - 13, getPlot(plotNum).getY() - 3,
				getPlot(plotNum).getZ() - 13);
		Location corner2 = new Location(player.getWorld(), getPlot(plotNum).getX() + 13, getPlot(plotNum).getY() + 31,
				getPlot(plotNum).getZ() + 14);

		Chunk c = corner1.getChunk();

		c.unload();
		corner1.getChunk().getWorld().setBiome(c.getX(), c.getZ(), translateBiome(itemName));
		c.getWorld().regenerateChunk(c.getX(), c.getZ());
		c.load();
		for (int i = 0; i < blocks.size(); i++) {
			// blocks.get(i).setBiome(translateBiome(itemName));

		}

		/*
		 * List<Chunk> chunks =
		 * Arrays.asList(WorldHelper.getPlayWorld().getLoadedChunks());
		 * List<net.minecraft.server.v1_8_R3.Chunk> normalizedChunks = chunks.;
		 * for(Player player1 : Bukkit.getOnlinePlayers()) {
		 * ReflectionUtils.sendPacket(player1, new
		 * PacketPlayOutMapChunk(((CraftChunk)player.getWorld().getChunkAt(
		 * player.getLocation().getBlock())).getHandle(), true, 0));
		 * ReflectionUtils.sendPacket(player1, new
		 * PacketPlayOutMapChunkBulk(normalizedChunks);
		 */
		// }

		/*
		 * PUT WORKING METHOD FOR RELOADING CHUNKS HERE
		 */

	}

	private static Biome translateBiome(String str) {
		switch (str) {
		case "plains":
			return Biome.PLAINS;

		case "desert":
			return Biome.DESERT;

		case "snow":
			return Biome.TAIGA;
		}

		return null;
	}

	public static void judgePlot(Player player, int currentPlot) {
		MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
		player.setPlayerTime(PlotHelper.getPlotTime(currentPlot), true);
		WorldHelper.setPlayerWeather(player, PlotHelper.getPlotWeather(currentPlot));
		player.teleport(PlotHelper.getPlot(currentPlot));
		corePlayer.sendTitleBar(0, 0, 0,
				ChatColor.translateAlternateColorCodes('&',
						"&6" + PlotHelper.getPlayerPlotName(currentPlot).getName() + "'s Plot"),
				ChatColor.translateAlternateColorCodes('&', "&6Build: &e" + BuildObjects.getRandomBuild()));
		GameUI.resetPointsGiven(player);
	}

	public static void displayPlotScoreboard() {
		  //pos 0: Rhin_ - 7
		  //pos 1: swag - 16
		//0,7,14,2
		
		List<Integer> winningPlots = InJudge.plotRating;
		
		ConcurrentHashMap<Integer, Integer> cooldown = new ConcurrentHashMap<>();
		
		/**
		 * Loony
		 */
		
		Map<String, Integer> votes = new HashMap<String, Integer>();
		Map.Entry<String, Integer> highest = null;
		Map.Entry<String, Integer> second = null;
		Map.Entry<String, Integer> third = null;
		
		List<Player> online = (List<Player>) Bukkit.getOnlinePlayers();
		for(int i = 0; i < online.size(); i++)
		{
			Player player = online.get(i);
			votes.put(player.getName(), InJudge.plotRating.get(i));
		}

		votes.entrySet().forEach(System.out::println);
		

		for(Map.Entry<String, Integer> entry : votes.entrySet())
		{
			if(highest == null || entry.getValue().compareTo(highest.getValue()) > 0)
			{
				highest = entry;
			}
		}
		
		votes.remove(highest.getKey());
		
		for(Map.Entry<String, Integer> entry : votes.entrySet())
		{
			if(second == null || entry.getValue().compareTo(second.getValue()) > 0)
			{
				second = entry;
			}
		}
		
		//I added these if statements
		if(second != null)
		votes.remove(second.getKey());
		
		for(Map.Entry<String, Integer> entry : votes.entrySet())
		{
			if(third == null || entry.getValue().compareTo(third.getValue()) > 0)
			{
				third = entry;
			}
		}
		
		if(third != null)
		votes.remove(third.getKey());
		
		
		votes.entrySet().forEach(System.out::println);
		
		for (Player player : Bukkit.getOnlinePlayers()) {

				player.sendMessage(ChatColor.GREEN + "===================" + ChatColor.GOLD + ChatColor.BOLD
						+ " Plot Scores " + ChatColor.GREEN + "===================");
				
				player.sendMessage(ChatColor.GOLD + "1) " + ChatColor.YELLOW + highest.getKey() + " with " + highest.getValue() + " vote(s)."); 
				if(second != null)
				player.sendMessage(ChatColor.GOLD + "2) " + ChatColor.YELLOW + second.getKey() + " with " + second.getValue() + " vote(s).");
				if(third != null)
				player.sendMessage(ChatColor.GOLD + "3) " + ChatColor.YELLOW + third.getKey() + " with " + third.getValue() + " vote(s)."); 
				
				int i = 3;
				for(String playerName : votes.keySet())
				{
					i++;
					if(playerName.equals(player.getName()))
						player.sendMessage(ChatColor.GOLD + "" + i + ") " + ChatColor.YELLOW + playerName + votes.get(playerName) + " vote(s).");
				}
				
//				for (int i = 0; i < winningPlots.size(); i++) {
//					player.sendMessage(ChatColor.GOLD + convertNum(i + 1) + " - " + ChatColor.YELLOW
//					+ getPlayerPlotName(i).getName() + ChatColor.GOLD + " with " + ChatColor.BOLD
//						+ winningPlots.get(i) + ChatColor.RESET + ChatColor.GOLD + " points");
//				}

			player.sendMessage(ChatColor.GREEN + "===================================================");
		}
	}

	private static String convertNum(int i) {

		if (i == 1)
			return "1st";

		if (i == 2)
			return "2nd";

		if (i == 3)
			return "3rd";

		return i + "th";
	}

}
