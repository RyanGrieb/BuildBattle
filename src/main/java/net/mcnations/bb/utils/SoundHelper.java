package net.mcnations.bb.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.world.PlotHelper;

public class SoundHelper {

	@SuppressWarnings("deprecation")
	public static void playRecord(Player p, Location loc, Material record) {
		// ((CraftPlayer)p).getHandle().playerConnection.sendPacket(new
		// PacketPlayOutWorldEvent(1005, new BlockPosition(loc.getBlockX(),
		// loc.getBlockY(), loc.getBlockZ()), record.getId(), false));
	}

	// public static HashMap<Material, Integer> recordDurations = new HashMap();

	public static void stopRecord(Player p, Location loc) {

	}

	public static void initializePlotSound() {
		/*
		 * recordDurations.put(Material.GOLD_RECORD, Integer.valueOf(178));
		 * recordDurations.put(Material.GREEN_RECORD, Integer.valueOf(185));
		 * recordDurations.put(Material.RECORD_3, Integer.valueOf(345));
		 * recordDurations.put(Material.RECORD_4, Integer.valueOf(185));
		 * recordDurations.put(Material.RECORD_5, Integer.valueOf(174));
		 * recordDurations.put(Material.RECORD_6, Integer.valueOf(197));
		 * recordDurations.put(Material.RECORD_7, Integer.valueOf(96));
		 * recordDurations.put(Material.RECORD_8, Integer.valueOf(150));
		 * recordDurations.put(Material.RECORD_9, Integer.valueOf(188));
		 * recordDurations.put(Material.RECORD_10, Integer.valueOf(251));
		 * recordDurations.put(Material.RECORD_11, Integer.valueOf(71));
		 * recordDurations.put(Material.RECORD_12, Integer.valueOf(235));
		 */

		BuildBattle.getCorePlugin().getServer().getScheduler().runTaskTimer(BuildBattle.getCorePlugin(),
				new Runnable() {
					public void run() {
						if (isInGame()) {
							setMusic();
						}
					}
				}, 0, 20 * 235);
	}

	public static void disableSound() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(BuildBattle.getCorePlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						// You can also get the location of the sound effect -
						// see PacketWrapper's WrapperPlayServerNamedSoundEffect

						// We can broadcast the soundname
						String soundName = event.getPacket().getStrings().read(0);

						// if(hasSoundDisabled(player)
						if (soundName.startsWith("mob.sheep.") //|| soundName.startsWith("fireworks.launch")
								|| soundName.startsWith("mob.villager."))
							event.setCancelled(true);
					}
				});

	}

	public static void setMusic() {
		for (int i = 0; i < PlotHelper.getPlotAmount(); i++) {
			Location loc = new Location(PlotHelper.getPlot(i).getWorld(), PlotHelper.getPlot(i).getX(),
					PlotHelper.getPlot(i).getY() - 15, PlotHelper.getPlot(i).getZ());
			PlotHelper.getPlot(i).getWorld().playEffect(loc, Effect.RECORD_PLAY, Material.RECORD_12);
		}
	}

	/*
	 * public static void playHubSound(Player player) {
	 * player.playEffect(WorldHelper.getHubLocation(), Effect.RECORD_PLAY,
	 * Material.RECORD_3); }
	 */

	static boolean isInGame() {
		if (BuildBattle.getGame().getCurrentState() != null)
			return (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_GAME")
					|| BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE"));

		return false;
	}

}
