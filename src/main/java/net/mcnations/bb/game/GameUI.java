package net.mcnations.bb.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.mcnations.bb.utils.item.Item;
import net.mcnations.bb.utils.item.SkullHelper;
import net.mcnations.bb.world.PlotHelper;
import net.mcnations.bb.world.WorldHelper;

public class GameUI {
	ItemStack CyanGlass = new ItemStack(Material.GLASS, 1, DyeColor.CYAN.getData());

	private static ItemStack hasNotVotedItem = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData());
	private static ItemStack hasVotedItem = new ItemStack(Material.STAINED_CLAY, 1, DyeColor.GREEN.getData());
	private static ItemStack plotOptionsItem = new ItemStack(Material.NETHER_STAR, 1);
	private static ItemStack pointsGivenItem = new ItemStack(Material.EMERALD, 1);

	public static List<ItemStack> judgeItems = new ArrayList<ItemStack>();
	public static Inventory plotOptionsInv;

	public static String[] names = { ChatColor.DARK_RED + "Poop", ChatColor.GOLD + "Meh...", ChatColor.YELLOW + "Ok...",
			ChatColor.GREEN + "Good", ChatColor.DARK_GREEN + "Amazing", ChatColor.DARK_BLUE + "Epic" };

	public static Material[] bannedItems = { Material.MONSTER_EGGS, Material.MONSTER_EGG, Material.EGG,
			Material.DRAGON_EGG };

	public static void giveJudgeItems(Player player) {
		judgeItems.clear();
		for (int i = 0; i < 6; i++) {
			ItemStack glassItem = new ItemStack(Material.STAINED_CLAY, 1, getColor(i).getData());

			ItemMeta m = glassItem.getItemMeta();
			m.setDisplayName(names[i]);
			glassItem.setItemMeta(m);

			player.getInventory().setItem(i, glassItem);
			judgeItems.add(glassItem);
		}

		player.getInventory().setItem(8, hasNotVotedItem());
		player.getInventory().setItem(9, pointsGivenItem());
	}

	public static void giveGameItems(Player player) {
		for (int i = 0; i < defaultItems().length; i++)
			player.getInventory().setItem(i, new ItemStack(defaultItems()[i]));

		player.getInventory().setItem(8, plotOptionsItem());
	}

	public static DyeColor getColor(int i) {
		switch (i) {
		case 0:
			return DyeColor.RED;

		case 1:
			return DyeColor.ORANGE;

		case 2:
			return DyeColor.YELLOW;

		case 3:
			return DyeColor.LIME;

		case 4:
			return DyeColor.GREEN;

		case 5:
			return DyeColor.BLUE;

		}

		return DyeColor.BLACK;
	}

	public static ItemStack hasNotVotedItem() {
		ItemMeta m = hasNotVotedItem.getItemMeta();
		m.setDisplayName(ChatColor.DARK_RED + "You have not voted!");
		hasNotVotedItem.setItemMeta(m);
		return hasNotVotedItem;
	}

	public static ItemStack hasVotedItem() {
		ItemMeta m = hasVotedItem.getItemMeta();
		m.setDisplayName(ChatColor.DARK_GREEN + "You have voted!");
		hasVotedItem.setItemMeta(m);
		return hasVotedItem;
	}

	public static ItemStack pointsGivenItem() {
		ItemMeta m = pointsGivenItem.getItemMeta();
		m.setDisplayName(ChatColor.GREEN + "Points: " + ChatColor.WHITE + "0");
		pointsGivenItem.setItemMeta(m);
		return pointsGivenItem;
	}

	public static ItemStack plotOptionsItem() {
		ItemMeta m = plotOptionsItem.getItemMeta();
		m.setDisplayName(ChatColor.GOLD + "Plot Options");
		m.setLore(Arrays.asList(ChatColor.GRAY + "Sound: Enabled", ChatColor.GRAY + "Placed Particles: 0"));
		plotOptionsItem.setItemMeta(m);
		return plotOptionsItem;
	}

	public static void resetPointsGiven(Player player) {
		ItemMeta m = pointsGivenItem.getItemMeta();
		m.setDisplayName(ChatColor.GREEN + "Points: " + ChatColor.WHITE + "2");
		pointsGivenItem.setItemMeta(m);
		player.getInventory().setItem(9, pointsGivenItem);
		player.getInventory().setItem(8, hasNotVotedItem());

	}

	public static void setItemPoints(Player player, int i) {
		ItemMeta m = pointsGivenItem.getItemMeta();
		m.setDisplayName(ChatColor.GREEN + "Points: " + ChatColor.WHITE + "" + i);
		pointsGivenItem.setItemMeta(m);

		player.getInventory().setItem(9, pointsGivenItem);
		// str = str.replaceAll("\\D+","");
	}

	public static int getPlayerItemPoints(Player player) {
		Inventory inv = player.getInventory();
		try {
			int itemPoints = Integer.parseInt(inv.getItem(9).getItemMeta().getDisplayName().replaceAll("\\D+", ""));

			return itemPoints;
		} catch (Exception e) {
			return 0;
		}
	}

	public static int getPlotRating(Player player) {

		return player.getInventory().getHeldItemSlot();
	}

	public static Inventory skullInventory() {
		Inventory inv = Bukkit.createInventory(null, 54, "Player Skulls");
		HashMap<ItemStack, String> skulls = new HashMap<>();
		
		skulls.put(SkullHelper.getSkull("ChazOfftopic"), "Dirt");
		skulls.put(SkullHelper.getSkull("GracefulNarwhal"), "Mycelium");
		skulls.put(SkullHelper.getSkull("MightyMega"), "Log");
		skulls.put(SkullHelper.getSkull("AlphaPieter1"), "Leaves");
		skulls.put(SkullHelper.getSkull("mescovic"), "Stone");
		skulls.put(SkullHelper.getSkull("Cobble"), "Cobblestone");
		skulls.put(SkullHelper.getSkull("acissejxd"), "Diamond Ore");
		//skulls.put(SkullHelper.getSkull("annayirb"), "Redstone Ore");
		skulls.put(SkullHelper.getSkull("Tereneckla"), "Emerald Ore");
		skulls.put(SkullHelper.getSkull("AllTheDiamond"), "Diamond Block");
		skulls.put(SkullHelper.getSkull("StackedGold"), "Gold Block");
		skulls.put(SkullHelper.getSkull("metalhedd"), "Iron Block");
		skulls.put(SkullHelper.getSkull("Maccys_Test_Acc"), "Redstone Block");
		skulls.put(SkullHelper.getSkull("Emeraldblock"), "Emerald Block");
		skulls.put(SkullHelper.getSkull("loiwiol"), "Obsidian");
		
		skulls.put(SkullHelper.getSkull("MHF_Pig"), "Pig");
		skulls.put(SkullHelper.getSkull("MHF_Cow"), "Cow");
		skulls.put(SkullHelper.getSkull("MHF_Chicken"), "Chicken");
		skulls.put(SkullHelper.getSkull("MHF_Wolf"), "Wolf");
		skulls.put(SkullHelper.getSkull("MHF_Ocelot"), "Ocelot");
		skulls.put(SkullHelper.getSkull("MHF_Sheep"), "Sheep");
		skulls.put(SkullHelper.getSkull("MHF_Squid"), "Squid");
		skulls.put(SkullHelper.getSkull("MHF_Villager"), "Villager");
		skulls.put(SkullHelper.getSkull("MHF_Slime"), "Slime");
		skulls.put(SkullHelper.getSkull("MHF_Creeper"), "Creeper");
		skulls.put(SkullHelper.getSkull("MHF_Zombie"), "Zombie");
		skulls.put(SkullHelper.getSkull("MHF_Skeleton"), "Skeleton");
		skulls.put(SkullHelper.getSkull("MHF_Wither"), "Wither");
		
		skulls.put(SkullHelper.getSkull("simbasbestbud"), "Hamburger");
		skulls.put(SkullHelper.getSkull("Crunchy_Taco34"), "Taco");
		skulls.put(SkullHelper.getSkull("Chipsandip"), "Nutella");
		skulls.put(SkullHelper.getSkull("MHF_Apple"), "Apple");
		skulls.put(SkullHelper.getSkull("ZachWarnerHD"), "Popcorn");
		skulls.put(SkullHelper.getSkull("Chazwell777"), "Chocolate Bar");
		skulls.put(SkullHelper.getSkull("_Grime"), "Loaf of Bread");
		skulls.put(SkullHelper.getSkull("QuadratCookie"), "Cookie");
		skulls.put(SkullHelper.getSkull("MHF_Cake"), "Cake");
		skulls.put(SkullHelper.getSkull("Cr4zzyGamer"), "Cheese");
		skulls.put(SkullHelper.getSkull("Kairu"), "Coconut");
		skulls.put(SkullHelper.getSkull("Weed_Pancakes"), "Pancakes");
		
		skulls.put(SkullHelper.getSkull("Fish181"), "Aquarium");
		skulls.put(SkullHelper.getSkull("Mapparere"), "Earth");
		skulls.put(SkullHelper.getSkull("Olaf_C"), "Clock");
		skulls.put(SkullHelper.getSkull("Addelburgh"), "Computer");
		skulls.put(SkullHelper.getSkull("sysfailure"), "TV");
		skulls.put(SkullHelper.getSkull("uioz"), "Radio");
		skulls.put(SkullHelper.getSkull("CS001"), "Stack of Books");
		skulls.put(SkullHelper.getSkull("CruXXx"), "Present");
		skulls.put(SkullHelper.getSkull("pearl_XD"), "Candle");
		
		
		
		/*ItemStack[] items = { SkullHelper.getSkull("AlphaPieter1"), SkullHelper.getSkull("Robbydeezle"),
				SkullHelper.getSkull("C418"), SkullHelper.getSkull("scemm"), SkullHelper.getSkull("Panda4994"),
				SkullHelper.getSkull("JL2579"), SkullHelper.getSkull("akaBruce"), SkullHelper.getSkull("annayirb"),
				SkullHelper.getSkull("Tereneckla"), SkullHelper.getSkull("pomi44"), SkullHelper.getSkull("Quartz"),
				SkullHelper.getSkull("Bendablob"), SkullHelper.getSkull("teachdaire"),
				SkullHelper.getSkull("metalhedd"), SkullHelper.getSkull("loiwiol"), SkullHelper.getSkull("rugofluk"),
				SkullHelper.getSkull("ZachWarnerHD"), SkullHelper.getSkull("ChoclateMuffin"),
				SkullHelper.getSkull("food"), SkullHelper.getSkull("CoderPuppy"), SkullHelper.getSkull("sysfailure"),
				SkullHelper.getSkull("uioz"), SkullHelper.getSkull("Edna_I"), SkullHelper.getSkull("KylexDavis"),
				SkullHelper.getSkull("Chuzard") };

		String[] itemNames = { "Leaves", "Rocks", "Music", "Dispenser", "Sticky Piston", "Piston", "Diamond Ore",
				"Redstone Ore", "Emerald Ore", "Sponge", "Quartz Block", "Hay", "Gold Block", "Iron Block", "Obsidian",
				"Sand", "Popcorn", "Muffin", "Hamburger", "Monitor", "TV", "Radio", "Ender Eye", "Apple", "Pokeball" };*/

		int i = 0;
		for (Map.Entry<ItemStack, String> entry : skulls.entrySet()) {
			ItemStack skullOwner = entry.getKey();
			String name = entry.getValue();
		    Item.setDescription(skullOwner, inv, ChatColor.RED + name, i);
		    i++;
		}
			

		return inv;
	}

	public static Inventory biomeInventory() {
		// Plains is defaut
		ItemStack[] items = { new ItemStack(Material.GRASS), new ItemStack(Material.SAND),
				new ItemStack(Material.SNOW) };

		String[] itemNames = { "Plains", "Desert", "Snow" };

		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.BLACK + "Plot Biome");

		for (int i = 0; i < items.length; i++)
			Item.setDescription(items[i], inv, ChatColor.GREEN + itemNames[i], 2 + (i * 2));

		return inv;
	}

	public static Inventory particleInventory() {
		ItemStack[] items = { new ItemStack(Material.SLIME_BALL), new ItemStack(Material.LAVA_BUCKET),
				new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.NOTE_BLOCK),
				new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.EMERALD),
				new ItemStack(Material.BLAZE_POWDER), new ItemStack(Material.ENCHANTMENT_TABLE) };

		String[] particleNames = { "Slime", "Lava Drip", "Water Drip", "Note", "Heart", "Happy Villager",
				"Angry Villager", "Glyph" };

		// add rest
		// first item starts at 10 last at 16.
		Inventory inv = Bukkit.createInventory(null, 36, ChatColor.BLACK + "Particle Options");
		for (int i = 0; i < items.length; i++)
			Item.setDescription(items[i], inv, ChatColor.GREEN + particleNames[i], 9 + i);
		return inv;
	}

	public static Inventory timeInventory(Player player) {
		ItemStack[] items = { new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()),
				new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()),
				new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()),
				new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()),
				new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()),
				new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()),
				new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()),
				new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()),
				new ItemStack(Material.STAINED_CLAY, 1, DyeColor.RED.getData()) };

		String[] particleNames = { "6 am", "9 am", "12 pm", "3 pm", "6 pm", "9 pm", "12 am", "3 am", "6 am" };

		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.BLACK + "Plot Time");

		for (int i = 0; i < items.length; i++)
			Item.setDescription(items[i], inv, ChatColor.GREEN + particleNames[i], i);

		for (int i = 0; i < inv.getSize(); i++) {
			if (ChatColor.stripColor(inv.getItem(i).getItemMeta().getDisplayName()).contains(ChatColor.stripColor(
					PlotHelper.convertPlotTime(PlotHelper.getPlotTime(PlotHelper.getPlayerPlotNumber(player)))))) {
				Item.setDescription(new ItemStack(Material.STAINED_CLAY, 1, DyeColor.GREEN.getData()), inv,
						inv.getItem(i).getItemMeta().getDisplayName(), i);
			}
		}

		return inv;
	}

	public static Inventory weatherInventory(Player player) {
		ItemStack[] items = { new ItemStack(Material.BLAZE_ROD, 1), new ItemStack(Material.WATER_BUCKET, 1),
				new ItemStack(Material.BUCKET, 1) };
		String[] itemNames = { "Thunderstorm", "Rain", "Clear" };

		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.BLACK + "Plot Weather");

		for (int i = 0; i < items.length; i++)
			Item.setDescription(items[i], inv, ChatColor.GREEN + itemNames[i], 2 + (i * 2));

		return inv;
	}

	public static Material[] defaultItems() {
		Material[] items = { Material.GRASS, Material.WOOD, Material.COBBLESTONE, Material.WOOL, Material.HARD_CLAY };
		return items;
	}

	public static void giveHubItems(Player player) {
		player.setGameMode(GameMode.ADVENTURE);
		player.getInventory().clear();
		player.setAllowFlight(false);
		player.teleport(WorldHelper.getHubLocation());
		player.setFoodLevel(20);
		player.getInventory().addItem(categoryItem());
		player.getInventory().setItem(8, backToHubItem());

	}

	private static ItemStack categoryItem() {
		ItemStack item = new ItemStack(Material.PAPER);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lCategories"));
		itemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7Voted for Nothing")));
		item.setItemMeta(itemMeta);

		return item;
	}

	private static ItemStack backToHubItem() {
		ItemStack item = new ItemStack(Material.ENDER_PEARL);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lReturn to Hub"));
		item.setItemMeta(itemMeta);

		return item;

	}

	public static Inventory categoriesInv() {
		// Change
		ItemStack[] items = { SkullHelper.getSkull("MHF_Apple"), SkullHelper.getSkull("Weed_Pancakes"),
				SkullHelper.getSkull("simbasbestbud"), SkullHelper.getSkull("MHF_Chicken"),
				SkullHelper.getSkull("ChazOfftopic"), SkullHelper.getSkull("jonasjonas2"),
				SkullHelper.getSkull("Addelburgh"), SkullHelper.getSkull("MHF_Guardian"), SkullHelper.getSkull("Eien15") };

		String[] itemNames = { "Fruits", "Breakfast", "Dinner", "Animals", "Landscape", "Medieval", "Modern", "Fantasy",
				"Gaming" };

		Inventory inv = Bukkit.createInventory(null, 18, ChatColor.BLACK + "Build Categories");

		for (int i = 0; i < items.length; i++)
			Item.setDescription(items[i], inv, ChatColor.DARK_PURPLE + itemNames[i], (i * 2));

		return inv;
	}

	public static int categorySize() {
		// Starting at 0.
		return (5 + 1);
	}

}
