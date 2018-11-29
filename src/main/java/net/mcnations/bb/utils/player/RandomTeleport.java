package net.mcnations.bb.utils.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import net.mcnations.bb.utils.RandomNumber;

public class RandomTeleport {

	public static List<Location> savedLocations = new ArrayList<Location>();

	public static void teleport(Player player, Location loc) {
		int x = (int) loc.getX();
		int y = (int) loc.getY();
		int z = (int) loc.getZ();
		
		Random yRandom = new Random();
		
		x = x + RandomNumber.getRandomNumber(6);
		y = y + yRandom.nextInt(6);
		z = z + RandomNumber.getRandomNumber(6);

		loc = new Location(loc.getWorld(), x, y, z);
		
		if(isLocationBad(loc))
		{
			teleport(player, loc);
			savedLocations.add(loc);
			return;
		}
		
		if (!savedLocations.contains(loc)) {
			player.teleport(loc);
			savedLocations.add(loc);
		} else
			teleport(player, loc);

	}
	
	private static boolean isLocationBad(Location loc)
	{
		//Return true if bad
		if(loc.getBlock().getType() != Material.AIR)
			return true;
		
		 for (int i = 1; i < 10; i++) {
             if (loc.getWorld().getBlockAt((int)loc.getX(), loc.getBlockY() + i, (int)loc.getY()).getType() != Material.AIR) {
                 return true;
             }
         }
		 
		 return false;
		
	}

}
