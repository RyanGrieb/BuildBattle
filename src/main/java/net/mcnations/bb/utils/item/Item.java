package net.mcnations.bb.utils.item;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Item {

	public static void setDescription(ItemStack item,Inventory inv,String name, int location) {
		ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        item.setItemMeta(im);
        inv.setItem(location, item);
	}
	
	public static void setDescription(ItemStack item,Inventory inv,String name, List<String> lore, int location) {
		ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        im.setLore(lore);
        item.setItemMeta(im);
        inv.setItem(location, item);
	}
	
	public static boolean isPlayerInventoryEmpty(Player player)
	{
		for(ItemStack item : player.getInventory().getContents())
		{
		    if(item != null)
		      return false;
		}
		return true;
	}
	
}
