package net.mcnations.bb.listeners;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.BuildObjects;
import net.mcnations.bb.game.GameUI;
import net.mcnations.bb.game.states.InJudge;
import net.mcnations.bb.game.states.InTiebreaker;
import net.mcnations.bb.utils.item.BannedItems;
import net.mcnations.bb.utils.item.Item;
import net.mcnations.bb.utils.particles.ParticleEffect;
import net.mcnations.bb.utils.particles.ParticleHelper;
import net.mcnations.bb.utils.player.BlockListener;
import net.mcnations.bb.utils.team.TeamHelper;
import net.mcnations.bb.world.PlotHelper;
import net.mcnations.bb.world.WorldHelper;
import net.mcnations.core.common.enums.GameType;
import net.mcnations.core.common.general.cache.MCNPlayerCache;
import net.mcnations.core.common.general.gameplayers.MCNPlayer;
import net.mcnations.core.common.utils.ServerRoute;

public class PlayerListener implements Listener {

	// Join
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent event) {

		Player player = event.getPlayer();
		MCNPlayer corePlayer = MCNPlayerCache.getCache(player.getUniqueId());
		TeamHelper.addBuildPlayer(player);

		// SoundHelper.playHubSound(player);

		corePlayer.sendTitleBar(20, 40, 20,
				ChatColor.translateAlternateColorCodes('&', "&6Welcome to &cBlock Builders!"), "");

		GameUI.giveHubItems(player);

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BuildBattle.getCorePlugin(), new Runnable() {
			public void run() {
				corePlayer.sendTitleBar(20, 40, 20, ChatColor.translateAlternateColorCodes('&', "&cVote for a theme!"),
						"&eUse the paper below to vote");
			}

		}, 20 * 5);

	}

	@EventHandler
	public void onPlayerDissconect(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		TeamHelper.removeBuildPlayer(player);

		// Remove the players categoryVote
		if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_LOBBY")) {
			String categoryName = ChatColor.stripColor(
					player.getInventory().getItem(0).getItemMeta().getLore().get(0).substring(12).toLowerCase());
			BuildObjects.removeCategoryVote(categoryName);
		}

		// Dont remove buildplayer ingame, set to null.
	}

	@EventHandler
	public void stopCropTrampling(PlayerInteractEvent event) {
		if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_LOBBY")
				|| BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE"))
			if (event.getAction() == Action.PHYSICAL) {
				Block block = event.getClickedBlock();

				if (block == null)
					return;

				int blockType = block.getTypeId();

				if (blockType == Material.SOIL.getId()) {
					event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
					event.setCancelled(true);

					block.setTypeId(blockType);
					block.setData(block.getData());
				}
			}
	}

	@EventHandler
	public static void PlayerAttack(EntityDamageByEntityEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDamage(final EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_GAME")
				&& player.getLocation().getY() > BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.y") + 26) {
			player.teleport(PlotHelper.getPlayerPlot(player));
		}

		if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE")
				&& player.getLocation().getY() > BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.y") + 26) {
			player.teleport(PlotHelper.getPlot(InJudge.getPlot(InJudge.currentPlot)));
		}

		if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_TIEBREAKER")
				&& player.getLocation().getY() > BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.y") + 26) {

			// Checks if were still in the juding phase of the tirebreaker state
			if (InTiebreaker.currentPlot != InTiebreaker.winningPlots.size())
				player.teleport(PlotHelper.getPlot(InTiebreaker.winningPlots.get(InTiebreaker.currentPlot)));
			else
				player.teleport(PlotHelper.getPlot(InJudge.winningPlot()));
		}

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (!PlotHelper.isBlockInBorder(block, player)) {
			player.sendMessage(ChatColor.RED + "Please build in your plot only");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void explodeEvent(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();

		// player.sendMessage(BlockListener.cooldown.get(player) + "");

		if (!PlotHelper.isBlockInBorder(block, player)) {
			player.sendMessage(ChatColor.RED + "Please build in your plot only");
			event.setCancelled(true);
			return;
		} else {
			if (BlockListener.placedBlocks.get(player) != null)
				BlockListener.placedBlocks.put(player, BlockListener.placedBlocks.get(player) + 1);
			else
				BlockListener.placedBlocks.put(player, 1);
		}
	}

	@EventHandler
	public void onItemDrop(BlockDispenseEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		// changed
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if (!Item.isPlayerInventoryEmpty(player))
				if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE")
						&& player.getInventory().getHeldItemSlot() < 6
						&& InJudge.currentPlot != PlotHelper.getPlayerPlotNumber(player)) {
					player.getInventory().setItem(8, player.getItemInHand());
					GameUI.setItemPoints(player, GameUI.getPlotRating(player));
					switch (player.getInventory().getHeldItemSlot()) {
					case 0:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 0.75F);
						break;
					case 1:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
						break;
					case 2:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1.25F);
						break;
					case 3:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1.5F);
						break;
					case 4:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1.75F);
						break;
					case 5:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 2.0F);
						break;
					}

				} else if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE")
						&& player.getInventory().getHeldItemSlot() < 6
						&& InJudge.currentPlot == PlotHelper.getPlayerPlotNumber(player))
					player.sendMessage(ChatColor.RED + "You cannot vote on your own plot!");

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if (!Item.isPlayerInventoryEmpty(player))
				if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_TIEBREAKER")
						&& player.getInventory().getHeldItemSlot() < 6 && InTiebreaker.winningPlots
								.get(InTiebreaker.currentPlot) != PlotHelper.getPlayerPlotNumber(player)) {
					player.getInventory().setItem(8, player.getItemInHand());
					GameUI.setItemPoints(player, GameUI.getPlotRating(player));
					switch (player.getInventory().getHeldItemSlot()) {
					case 0:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 0.75F);
						break;
					case 1:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
						break;
					case 2:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1.25F);
						break;
					case 3:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1.5F);
						break;
					case 4:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1.75F);
						break;
					case 5:
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 2.0F);
						break;
					}

				} else if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_TIEBREAKER")
						&& player.getInventory().getHeldItemSlot() < 6 && InTiebreaker.winningPlots
								.get(InTiebreaker.currentPlot) == PlotHelper.getPlayerPlotNumber(player))
					player.sendMessage(ChatColor.RED + "You cannot vote on your own plot!");

	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {
			Material item = event.getCursor().getType();
			for (int i = 0; i < GameUI.bannedItems.length; i++)
				if (item == GameUI.bannedItems[i]) {
					event.setCancelled(true);
					event.getWhoClicked().sendMessage(ChatColor.RED + "That item is blacklisted..");
				}
		} catch (Exception e) {
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		try {
			Material item = event.getCursor().getType();
			for (int i = 0; i < GameUI.bannedItems.length; i++)
				if (item == GameUI.bannedItems[i]
						&& !event.getWhoClicked().getInventory().getName().contains("Plot Options")) {
					event.setCancelled(true);
					event.getWhoClicked().sendMessage(ChatColor.RED + "That item is blacklisted..");
				}
		} catch (Exception e) {
		}
	}

	@EventHandler
	public void onClickSlot(InventoryClickEvent event) {
		try {

			if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_LOBBY")) {
				event.setCancelled(true);
				return;
			}

			if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_JUDGE")
					|| (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_GAME")
							&& event.getCurrentItem().getType() == Material.NETHER_STAR))
				event.setCancelled(true);
		} catch (Exception e) {
		}
	}

	@EventHandler
	public void optionsUI(PlayerInteractEvent e) {
		try {
			Player player = e.getPlayer();
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				if (player.getItemInHand().getItemMeta().getDisplayName().contains("Plot Options")) {
					GameUI.plotOptionsInv = Bukkit.createInventory(null, 36, ChatColor.BLACK + "Plot Options");
					Item.setDescription(PlotHelper.getPlotFloor(PlotHelper.getPlayerPlotNumber(player)),
							GameUI.plotOptionsInv, ChatColor.GOLD + "Plot Floor", 15);
					Item.setDescription(new ItemStack(Material.BLAZE_POWDER, 1, DyeColor.WHITE.getData()),
							GameUI.plotOptionsInv, ChatColor.GOLD + "Plot Particles", 13);
					Item.setDescription(new ItemStack(Material.WATCH, 1), GameUI.plotOptionsInv,
							ChatColor.GOLD + "Plot Time", 11);
					Item.setDescription(new ItemStack(Material.WATER_BUCKET, 1), GameUI.plotOptionsInv,
							ChatColor.GOLD + "Plot Weather", 29);
					Item.setDescription(new ItemStack(Material.SKULL_ITEM, 1), GameUI.plotOptionsInv,
							ChatColor.GOLD + "Player Heads", 31);
					player.openInventory(GameUI.plotOptionsInv);
				}
		} catch (Exception asdf) {
		}
	}

	@EventHandler
	public void categoryUI(PlayerInteractEvent e) {
		try {
			Player player = e.getPlayer();
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				if (ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName())
						.contains("Categories")) {

					player.openInventory(GameUI.categoriesInv());
				}
		} catch (Exception asdf) {
		}
	}

	@EventHandler
	public void onPlotTimeClick(final InventoryClickEvent e) {
		try {
			final Player player = (Player) e.getWhoClicked();
			ItemStack item = e.getCurrentItem();
			int plotNumber = PlotHelper.getPlayerPlotNumber(player);
			if (e.getInventory().getTitle().contains("Plot Time")) {
				// check item name of item that was clicked & set it to our plot
				// time.
				PlotHelper.setPlotTime(plotNumber,
						PlotHelper.convertPlotTime(ChatColor.stripColor(item.getItemMeta().getDisplayName())));
				player.setPlayerTime(
						PlotHelper.convertPlotTime(ChatColor.stripColor(item.getItemMeta().getDisplayName())), true);
				player.sendMessage(ChatColor.GOLD + "Plot Time Changed");
				e.setCancelled(true);
				player.closeInventory();

			}
		} catch (Exception ex) {
		}

	}

	/*
	 * @EventHandler public void onPlotBiomeClick(final InventoryClickEvent e) {
	 * try{ final Player player = (Player) e.getWhoClicked(); ItemStack item =
	 * e.getCurrentItem(); int plotNumber =
	 * PlotHelper.getPlayerPlotNumber(player); String itemName =
	 * ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase());
	 * if (e.getInventory().getTitle().contains("Plot Biome")) {
	 * PlotHelper.setPlotBiome(player, itemName);
	 * player.sendMessage(ChatColor.GOLD + "Plot Biome Changed");
	 * e.setCancelled(true); player.closeInventory(); } }catch(Exception ex){} }
	 */
	@EventHandler
	public void onPlayerSkullClick(final InventoryClickEvent e) {
		if (e.getInventory().getTitle().contains("Player Skulls")) {
			e.setCancelled(true);
			Player player = (Player) e.getWhoClicked();
			ItemStack i = e.getCurrentItem();
			player.getInventory().addItem(i);
			player.closeInventory();
		}
	}

	@EventHandler
	public void onPlotWeatherClick(InventoryClickEvent e) {
		try {
			final Player player = (Player) e.getWhoClicked();
			ItemStack item = e.getCurrentItem();
			int plotNumber = PlotHelper.getPlayerPlotNumber(player);
			String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase());
			if (e.getInventory().getTitle().contains("Plot Weather")) {
				PlotHelper.setPlotWeather(plotNumber, itemName);
				WorldHelper.setPlayerWeather(player, itemName);
				player.sendMessage(ChatColor.GOLD + "Plot Weather Changed");
				e.setCancelled(true);
				player.closeInventory();

			}
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void onCategoryClick(InventoryClickEvent e) {

		if (!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null
				|| e.getClickedInventory().getTitle() == null || e.getCurrentItem() == null
				|| e.getCurrentItem().getType() == Material.AIR)
			return;

		if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_LOBBY")
				&& ChatColor.stripColor(e.getInventory().getTitle()).contains("Categories")) {
			Player player = (Player) e.getWhoClicked();

			String itemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase());
			String categoryName = ChatColor
					.stripColor(player.getInventory().getItem(0).getItemMeta().getLore().get(0).substring(12).toLowerCase());

			if (categoryName.equals("nothing")) {
				BuildObjects.addCategoryVote(itemName);
			} else if (!categoryName.equals("nothing")) {

				BuildObjects.removeCategoryVote(categoryName);

				BuildObjects.addCategoryVote(itemName);
			}

			if (player.getInventory().getItem(0) != null)
				Item.setDescription(player.getInventory().getItem(0), player.getInventory(),
						player.getInventory().getItem(0).getItemMeta().getDisplayName(),
						Arrays.asList(ChatColor.translateAlternateColorCodes('&',
								"&7Voted for " + e.getCurrentItem().getItemMeta().getDisplayName())),
						0);

			player.sendMessage(ChatColor.GOLD + "You have voted for the "
					+ ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()) + " category");
			e.setCancelled(true);
			player.closeInventory();

		}
	}

	@EventHandler
	public void onPlotFloorClick(final InventoryClickEvent e) {
		final Player player = (Player) e.getWhoClicked();
		try {
			if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Plot Floor")) {

				Bukkit.getServer().getScheduler().runTaskLater(BuildBattle.getCorePlugin(), new Runnable() {
					public void run() {
						if (GameUI.plotOptionsInv.getItem(15) != null
								&& (GameUI.plotOptionsInv.getItem(15).getTypeId() < 193
										|| GameUI.plotOptionsInv.getItem(15).getTypeId() == 326
										|| GameUI.plotOptionsInv.getItem(15).getTypeId() == 327)
								&& !BannedItems.items.contains(GameUI.plotOptionsInv.getItem(15).getTypeId())) {

							int typeID = Integer.parseInt(GameUI.plotOptionsInv.getItem(15).getData().toString()
									.substring(GameUI.plotOptionsInv.getItem(15).getData().toString().indexOf('(') + 1,
											GameUI.plotOptionsInv.getItem(15).getData().toString().length() - 1));
							PlotHelper.replaceFloor(GameUI.plotOptionsInv.getItem(15),
									PlotHelper.getPlayerPlotNumber(player), typeID);
							player.sendMessage(ChatColor.GOLD + "Plot floor changed.");

						} else {
							e.getWhoClicked().closeInventory();
							e.setCancelled(true);
							player.sendMessage(ChatColor.GOLD + "Not a valid item.");
							return;
						}
					}
				}, 5);

				Bukkit.getServer().getScheduler().runTaskLater(BuildBattle.getCorePlugin(), new Runnable() {
					public void run() {
						e.getWhoClicked().closeInventory();
						e.setCancelled(true);
						// player.sendMessage(ChatColor.GOLD + "Plot floor
						// changed.");
					}
				}, 10);

			}

			if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Plot Particles")) {
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				player.openInventory(GameUI.particleInventory());
			}

			if (e.getInventory().getTitle().contains("Particle Options")
					&& e.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.GREEN + "")
					&& !player.getInventory().contains(e.getCurrentItem())) {
				player.getInventory().addItem(e.getCurrentItem());
				e.getWhoClicked().closeInventory();
				e.setCancelled(true);
			}

			if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Plot Time")) {
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				player.openInventory(GameUI.timeInventory(player));
			}

			if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Plot Biome")) {
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				player.sendMessage(ChatColor.RED + "Comming Soon...");
			}

			if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Plot Weather")) {
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				player.openInventory(GameUI.weatherInventory(player));
			}

			if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Player Heads")) {
				e.setCancelled(true);
				e.getWhoClicked().closeInventory();
				player.openInventory(GameUI.skullInventory());
			}

			/*
			 * if (e.getCurrentItem().getItemMeta().getDisplayName().contains(
			 * "Toggle Music")) { String itemName =
			 * ChatColor.stripColor(player.getInventory().getItem(8).getItemMeta
			 * ().getLore().get(0)); String soundSetting =
			 * itemName.substring(itemName.indexOf(" ") + 1); ItemStack item =
			 * player.getInventory().getItem(8); ItemMeta m =
			 * item.getItemMeta(); switch (soundSetting) { case "Enabled":
			 * 
			 * m.setLore(Arrays.asList(ChatColor.GRAY + "Sound: Disabled"));
			 * item.setItemMeta(m); player.sendMessage(ChatColor.GOLD+
			 * "Sound Disabled"); //Song doesnt stop b/c the player is in a
			 * different location SoundHelper.stopRecord(player,
			 * player.getLocation()); break;
			 * 
			 * case "Disabled": m.setLore(Arrays.asList(ChatColor.GRAY +
			 * "Sound: Enabled")); item.setItemMeta(m);
			 * player.sendMessage(ChatColor.GOLD+"Sound Enabled");
			 * //SoundHelper.playRecord(player, player.getLocation(),
			 * Material.RECORD_4); break; }
			 * 
			 * e.getWhoClicked().closeInventory(); e.setCancelled(true); }
			 */
		} catch (Exception ex) {
		}
	}

	@EventHandler
	public void onParticleAdd(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack block = event.getItem();
		try {
			if (block != null) {
				String name = ChatColor.stripColor(block.getItemMeta().getDisplayName());

				if (event.getAction() == Action.RIGHT_CLICK_BLOCK
						&& block.getItemMeta().getDisplayName().contains(ChatColor.GREEN + "")) {
					event.setCancelled(true);
				}

				if (block.getItemMeta().getDisplayName().contains(ChatColor.GREEN + "")) {

					ItemStack item = player.getInventory().getItem(8);
					ItemMeta im = item.getItemMeta();

					int placedParticles = Integer.parseInt(ChatColor.stripColor(im.getLore().get(1).substring(20)));

					if (placedParticles >= 25) {
						player.sendMessage(ChatColor.GOLD + "You have placed the maximum amount of particles!");
						return;
					}

					im.setLore(Arrays.asList(im.getLore().get(0),
							ChatColor.GRAY + "Placed Particles: " + (placedParticles + 1)));

					item.setItemMeta(im);

					switch (name) {
					case "Slime":
						ParticleHelper.addParticle(ParticleEffect.SLIME, player.getLocation());
						break;

					case "Lava Drip":
						ParticleHelper.addParticle(ParticleEffect.DRIP_LAVA, player.getLocation());
						for (int i = 0; i < ParticleHelper.cloudSize; i++)
							ParticleHelper.addParticle(ParticleEffect.DRIP_LAVA,
									ParticleHelper.getCloudLocation(player, i));
						break;

					case "Water Drip":

						for (int i = 0; i < ParticleHelper.cloudSize; i++)
							ParticleHelper.addParticle(ParticleEffect.DRIP_WATER,
									ParticleHelper.getCloudLocation(player, i));
						break;

					case "Note":
						ParticleHelper.addParticle(ParticleEffect.NOTE, player.getLocation());
						break;

					case "Heart":
						ParticleHelper.addParticle(ParticleEffect.HEART, player.getLocation());
						break;

					case "Happy Villager":
						ParticleHelper.addParticle(ParticleEffect.VILLAGER_HAPPY, player.getLocation());
						break;

					case "Angry Villager":
						ParticleHelper.addParticle(ParticleEffect.VILLAGER_ANGRY, player.getLocation());
						break;

					case "Glyph":
						ParticleHelper.addParticle(ParticleEffect.ENCHANTMENT_TABLE, player.getLocation());
						break;

					case "Bubble":
						ParticleHelper.addParticle(ParticleEffect.WATER_BUBBLE, player.getLocation());
						break;

					default:
						return;

					}
				}
			}
		} catch (NullPointerException e) {
		}
	}

	@EventHandler
	public void onMinecartClick(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity instanceof HopperMinecart) {
			event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to interact with hopper minecarts!");
			event.setCancelled(true);
		}
		if (entity instanceof StorageMinecart) {
			event.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to interact with storage minecarts!");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void bannedItemInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (block.getTypeId() == 154) {
				event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to interact with this block");
				event.setCancelled(true);
			}
			if (block.getTypeId() == 146) {
				event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to interact with this block");
				event.setCancelled(true);
			}
			if (block.getTypeId() == 158) {
				event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to interact with this block");
				event.setCancelled(true);
			}
			if (block.getTypeId() == 408) {
				event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to interact with this block");
				event.setCancelled(true);
			}
			if (block.getTypeId() == 342) {
				event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to interact with this block");
				event.setCancelled(true);
			}
			if (block.getTypeId() == 54) {
				event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to interact with this block");
				event.setCancelled(true);
			}
			if (block.getTypeId() == 346) {
				event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to interact with this item");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerFishingEvent(PlayerFishEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void stopBoatPlace(PlayerInteractEvent event) {
		try {
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (event.getItem().getType() == Material.BOAT) {
					event.setCancelled(true);
				}

			}
		} catch (Exception e) {
		}
	}

	@EventHandler
	public void stopMinecartPlace(PlayerInteractEvent event) {
		try {
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (event.getItem().getType() == Material.MINECART) {
					event.setCancelled(true);
				}
			}
		} catch (Exception e) {
		}
	}

	@EventHandler
	public void stopPistonPushEvent(BlockPistonExtendEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void stopRedstoneEvent(BlockRedstoneEvent event) {
		event.setNewCurrent(0);
	}

	// Changed
	@EventHandler
	public void onClickSlotHub(InventoryClickEvent e) {
		if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_LOBBY")) {
			e.setResult(Result.DENY);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBucketPlace(PlayerBucketEmptyEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlockClicked().getRelative(BlockFace.UP);

		// player.sendMessage(BlockListener.cooldown.get(player) + "");

		if (!PlotHelper.isBlockInBorder(block, player)) {
			player.sendMessage(ChatColor.RED + "Please build in your plot only");
			e.setCancelled(true);
			return;
		} else {
			if (BlockListener.placedBlocks.get(player) != null)
				BlockListener.placedBlocks.put(player, BlockListener.placedBlocks.get(player) + 1);
			else
				BlockListener.placedBlocks.put(player, 1);
		}
	}

	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		if (event.getCause() != IgniteCause.FLINT_AND_STEEL)
			event.setCancelled(true);
	}

	@EventHandler
	public void backToHub(PlayerInteractEvent event) {
		if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_LOBBY"))
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			if (event.getItem().getType().equals(Material.ENDER_PEARL)) {
				Player p = event.getPlayer();
				
				ServerRoute.routeToBestServer(p, GameType.HUB, true);	
				
				event.setCancelled(true);

			}
	}
	
	@EventHandler
	public void onVillagerClick(PlayerInteractEntityEvent e) {
		if (e.getRightClicked().getType() == EntityType.VILLAGER) {
			e.setCancelled(true);
			e.getPlayer().updateInventory();
			if (BuildBattle.getGame().getCurrentState().getRawName().equals("IN_LOBBY"))
				e.getPlayer().openInventory(GameUI.categoriesInv());
		}
	}
}
