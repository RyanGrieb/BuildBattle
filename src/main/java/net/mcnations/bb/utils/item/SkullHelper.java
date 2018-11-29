package net.mcnations.bb.utils.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullHelper {

	
	public static ItemStack getSkull(String owner)
	{
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
	      skull.setDurability((short)3);
	      SkullMeta meta = (SkullMeta)skull.getItemMeta();
	      meta.setOwner(owner);
	      skull.setItemMeta(meta);
	      
	      return skull;
	}
	
}
