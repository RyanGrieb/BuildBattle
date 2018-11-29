package net.mcnations.bb.utils.player;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BlockListener {

	public static ConcurrentHashMap<Player, Integer> placedBlocks = new ConcurrentHashMap<>();

	public static boolean requiredBlocks(Player player) {
		if(placedBlocks.get(player) == null)
			return false;
		
		if (placedBlocks.get(player) < 9)
			return false;

		return true;
	}
	
	public static boolean noOneBuilt() {
		for (Player player : Bukkit.getOnlinePlayers())
			if (BlockListener.requiredBlocks(player))
				return false;

		return true;
	}
	
	public static boolean onlyOneBuilt() {
		int sum = 0;
		
		for (Player player : Bukkit.getOnlinePlayers())
			if (BlockListener.requiredBlocks(player))
				sum++;

		return (sum == 1);
	}

	public static boolean didPlayerBuild(Player playerPlotName) {
		return placedBlocks.get(playerPlotName) > 9;
	}

}
