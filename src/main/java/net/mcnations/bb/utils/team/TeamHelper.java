package net.mcnations.bb.utils.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

public class TeamHelper {

	public static List<Player> buildplayers = new ArrayList<Player>();
	public static List<Player> oldplayer = new ArrayList<Player>();
	
	public static void addBuildPlayer(Player player)
	{
		buildplayers.add(player);
	}
	
	public static void addOldPlayer(Player player)
	{
		oldplayer.add(player);
	}
	
	public static void clearOldPlayer()
	{
		oldplayer.clear();
	}
	
	public static int oldPlayerSize()
	{
		return oldplayer.size();
	}
	
	public static void removeBuildPlayer(Player player)
	{
		if(buildplayers.contains(player))
			buildplayers.remove(player);
			//buildplayers.set(buildplayers.indexOf(player),null);
	}
	
	public boolean isBuildPlayer(Player player)
	{
		if(buildplayers.contains(player))
			return true;
		else
			return false;
	}
	
	public static int buildPlayerSize()
	{
		return buildplayers.size();
	}
	
	
	
}
