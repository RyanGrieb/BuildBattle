package net.mcnations.bb.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.utils.entity.SpawnVillager;
import net.mcnations.bb.utils.player.BattlePlayer;
import net.mcnations.bb.utils.player.PlayerCache;
import net.mcnations.bb.world.WorldHelper;
import net.mcnations.core.CorePlugin;

public class GameCommands extends BukkitCommand {

	CorePlugin corePlugin;

	public GameCommands(CorePlugin corePlugin, String name) {
		super(name);
		this.corePlugin = corePlugin;
		this.description = "Multiple Game commands";
		this.usageMessage = "/be <subcommand>";
		this.setPermission("mcn.rank.staff");
		this.setAliases(new ArrayList<>());
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if ((sender instanceof Player)) {
			Player player = (Player) sender;
			if (label.equalsIgnoreCase("bb") && player.isOp()) {
				if (args.length > 0) {
					BattlePlayer battlePlayer = PlayerCache.getPlayerCache(player.getUniqueId());

					if (args[0].toLowerCase().startsWith("startgame") && player.isOp()) {
						BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(1));
						player.sendMessage(ChatColor.GOLD + "Game Started");
					}

					if (args[0].toLowerCase().startsWith("stopgame") && player.isOp()) {
						BuildBattle.getGame().setState(BuildBattle.getGame().getAllStates().get(0));
						player.sendMessage(ChatColor.GOLD + "Game Stopped");
					}
					
					if(args[0].equalsIgnoreCase("spawnvillager"))
					{
						SpawnVillager.load();
					}
					
					if(args[0].equalsIgnoreCase("removevillager"))
					{
						for(Entity en : WorldHelper.getHubWorld().getEntities())
							en.remove();
					}
					

				}
			}

			if (label.contains("report")) {
				player.sendMessage(ChatColor.GOLD + "Report Sent!");
			}

		}
		return false;
	}
}
