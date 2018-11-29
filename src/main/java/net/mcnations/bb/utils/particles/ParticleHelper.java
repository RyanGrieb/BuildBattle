package net.mcnations.bb.utils.particles;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.mcnations.bb.BuildBattle;

public class ParticleHelper {

	//Combine these into a hashmap later
	private static List<ParticleEffect> particleList = new ArrayList<ParticleEffect>();
	private static List<Location> particleLocations = new ArrayList<Location>();

	public static int cloudSize = 9;
	//recode this later, please.. d:
	public static Location getCloudLocation(Player player, int i) {
		Location[] loc = {
				new Location(player.getWorld(), player.getLocation().getX() - 0.5, player.getLocation().getY(),
						player.getLocation().getZ()),
				new Location(player.getWorld(), player.getLocation().getX() - .25, player.getLocation().getY(),
						player.getLocation().getZ() - 0.25),
				new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() - .25,
						player.getLocation().getZ()),
				new Location(player.getWorld(), player.getLocation().getX() + .25, player.getLocation().getY() + .65,
						player.getLocation().getZ() - 0.5),
				new Location(player.getWorld(), player.getLocation().getX() + .5, player.getLocation().getY() + .25,
						player.getLocation().getZ() - 0.25),
				new Location(player.getWorld(), player.getLocation().getX() + .25, player.getLocation().getY() - .5,
						player.getLocation().getZ() + 0.25),
				new Location(player.getWorld(), player.getLocation().getX() - .5, player.getLocation().getY() + .5,
						player.getLocation().getZ() - 0.25),
				new Location(player.getWorld(), player.getLocation().getX() - .25, player.getLocation().getY() + .25,
						player.getLocation().getZ() - 0.25),
				new Location(player.getWorld(), player.getLocation().getX() - .75, player.getLocation().getY(),
						player.getLocation().getZ() - 0.25),
				new Location(player.getWorld(), player.getLocation().getX() + .10, player.getLocation().getY(),
						player.getLocation().getZ() - 0.5) };
		
		return loc[i];
	}

	public static void initilizeParticleHelper() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BuildBattle.getCorePlugin(), new Runnable() {
			public void run() {

				if (BuildBattle.getGame().getCurrentState() != null)
				if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_GAME") || BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE"))
					for (int i = 0; i < particleList.size(); i++) { // ParticleEffect.DRIP_WATER
						particleList.get(i).display(0, 0, 0, 1, 5, particleLocations.get(i), 100);
					}
				else {
					particleList.clear();
					particleLocations.clear();
				}

			}
		}, 0, 9);
	}

	public static void addParticle(ParticleEffect pe, Location loc) {
		particleList.add(pe);
		particleLocations.add(loc);
	}

}
