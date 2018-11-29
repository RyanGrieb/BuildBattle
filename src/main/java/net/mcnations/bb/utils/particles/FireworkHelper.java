package net.mcnations.bb.utils.particles;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class FireworkHelper {

	public static void spawnFirework(Location loc, Type type, Color color) {
		Firework f = (Firework) loc.getWorld().spawn(loc, Firework.class);

		FireworkMeta fm = f.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(type).withColor(color).withFade(color)
				.build());
		fm.setPower(1);
		f.setFireworkMeta(fm);
	}

	public static Color fireworkColorTranslator(String str) {
		switch (str) {
		case "blue":
			return Color.BLUE;
		case "red":
			return Color.RED;
		case "green":
			return Color.GREEN;
		case "yellow":
			return Color.YELLOW;
		default:
			return Color.GRAY;
		}
	}

	public static Color getRandomColor(int nextInt) {
		Random rnd = new Random();
		int num = rnd.nextInt(nextInt);
		switch (num) {
		case 0:
			return Color.AQUA;
		case 1:
			return Color.BLACK;
		case 2:
			return Color.BLUE;
		case 3:
			return Color.FUCHSIA;
		case 4:
			return Color.GRAY;
		case 5:
			return Color.GREEN;
		case 6:
			return Color.LIME;
		case 7:
			return Color.MAROON;
		case 8:
			return Color.NAVY;
		case 9:
			return Color.OLIVE;
		case 10:
			return Color.ORANGE;
		case 11:
			return Color.PURPLE;
		case 12:
			return Color.RED;
		case 13:
			return Color.SILVER;
		case 14:
			return Color.TEAL;
		case 15:
			return Color.WHITE;
		case 16:
			return Color.YELLOW;
		}

		return Color.GRAY;

	}

}
