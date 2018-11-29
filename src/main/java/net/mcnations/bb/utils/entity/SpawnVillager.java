package net.mcnations.bb.utils.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.world.WorldHelper;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class SpawnVillager {

	public static List<Villager> villager = new ArrayList<Villager>();

	public static void load() {
		Bukkit.getServer().getScheduler().runTaskLater(BuildBattle.getCorePlugin(), new Runnable() {
			public void run() {
		SpawnVillager.initilizeVillager();
			}
		},20*3);
	}
	
	public static void initilizeVillager() {
		Villager kitVillager = WorldHelper.getHubWorld()
				.spawn(getHubLoc(ChatColor.YELLOW + "Choose your theme!",
						BuildBattle.getCorePlugin().getCorePlugin().getConfig().getInt("Villager.x"),
						BuildBattle.getCorePlugin().getConfig().getInt("Villager.y"),
						BuildBattle.getCorePlugin().getConfig().getInt("Villager.z")), Villager.class);
		freezeEntity(kitVillager);
	}

	public static Location getHubLoc(String holoName, double xDiff, double yDiff, double zDiff) {
		final Location loc = new Location(WorldHelper.getHubWorld(),
				BuildBattle.getCorePlugin().getConfig().getInt("Hub.x") + xDiff,
				BuildBattle.getCorePlugin().getConfig().getInt("Hub.y") + yDiff,
				BuildBattle.getCorePlugin().getConfig().getInt("Hub.z") + zDiff, BuildBattle.getCorePlugin().getConfig().getInt("Villager.yaw"), 0);

		final Location holoLoc = new Location(WorldHelper.getHubWorld(),
				BuildBattle.getCorePlugin().getConfig().getInt("Hub.x") + xDiff,
				(BuildBattle.getCorePlugin().getConfig().getInt("Hub.y") + yDiff) + 3,
				BuildBattle.getCorePlugin().getConfig().getInt("Hub.z") + zDiff);

		Hologram hologram = HologramsAPI.createHologram(BuildBattle.getCorePlugin(), holoLoc);
		hologram.appendTextLine(holoName);

		// world.spawnEntity(new Location(null, x, y, z, yaw, pitch),
		// EntityType.ZOMBIE);

		return loc;
	}
	
	
	public static void freezeEntity(Entity en){
		net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) en).getHandle();
	      NBTTagCompound compound = new NBTTagCompound();
	      nmsEn.c(compound);
	      compound.setByte("NoAI", (byte) 1);
	      nmsEn.f(compound);
	  }

}
