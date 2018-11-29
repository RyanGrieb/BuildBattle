package net.mcnations.bb;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import net.mcnations.bb.commands.GameCommands;
import net.mcnations.bb.game.Game;
import net.mcnations.bb.listeners.PlayerListener;
import net.mcnations.bb.utils.SoundHelper;
import net.mcnations.bb.utils.TimerHelper;
import net.mcnations.bb.utils.entity.SpawnVillager;
import net.mcnations.bb.utils.particles.ParticleHelper;
import net.mcnations.bb.world.WorldHelper;
import net.mcnations.core.CorePlugin;
import net.mcnations.core.common.enums.GameType;
import net.mcnations.core.common.features.achievements.AchievementsAttribute;
import net.mcnations.core.common.features.chat.ChatAttribute;
import net.mcnations.core.common.features.cosmetics.CosmeticsAttribute;
import net.mcnations.core.common.features.gadgets.GadgetsAttribute;
import net.mcnations.core.common.features.guild.GuildAttribute;
import net.mcnations.core.common.features.mounts.MountsAttribute;
import net.mcnations.core.common.features.npc.NPCAttribute;
import net.mcnations.core.common.features.party.PartyAttribute;
import net.mcnations.core.common.features.pets.PetsAttribute;
import net.mcnations.core.common.features.profile.ProfileAttribute;
import net.mcnations.core.common.features.rscpack.ResourceAttribute;
import net.mcnations.core.common.features.statistics.StatisticsAttribute;
import net.mcnations.core.database.DatabaseConfig;
import net.mcnations.core.database.server.CoreServer;
import net.mcnations.core.database.server.ServerManager;
import net.mcnations.core.engine.logger.GameLogger;

public class BuildBattle extends CorePlugin {

	protected static Game game;
	
	public BuildBattle() {
		super(GameType.BUILD_BATTLE);
		
		FileConfiguration database = DatabaseConfig.getConfig();
		this.coreServer = new CoreServer(database.getString("server.id"), database.getString("server.name"),
				getServer().getOnlinePlayers().size(), getServer().getMaxPlayers(), "IN_LOBBY",
				database.getString("server.host"), database.getInt("server.port"), database.getString("server.extra"),
				getGameType());
	}

	@Override
	public void enabled() {
		saveDefaultConfig();
		
		game = new Game(new GameLogger(), "Build Battle", getCorePlugin(), getCorePlugin());
		CompletableFuture.runAsync(ServerManager::updateServer);
		
		//this.getServer().getPluginManager().registerEvents(new RegisterAccount(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		
		WorldHelper.deleteWorld(false);
        new BukkitRunnable()
        {
            public void run()
            {
                WorldHelper.loadWorld();
            }
        }.runTaskLater(this, 2 * 20L);
		
        SpawnVillager.load();
        
        ParticleHelper.initilizeParticleHelper();
        TimerHelper.addBossBar();
        TimerHelper.judgeItemsActionBar();
        TimerHelper.fireworkTimer();
        SoundHelper.disableSound();
        SoundHelper.initializePlotSound();
        
		getLogger().info("has been enabled.");
	}

	@Override
	public void disable() {

		CompletableFuture.runAsync(ServerManager::updateServer);
		getLogger().info("has been disabled.");
	}

	@Override
	public void registerAttributes() {
		if (!getCorePlugin().getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			// Shut down
			getLogger().severe("ProtocolLib Not Found!");
			getCorePlugin().getServer().shutdown();
		}


		getCorePlugin().registerAttributes(true, new ResourceAttribute(this), new ChatAttribute(this),
				new GuildAttribute(this), new PartyAttribute(this), new ProfileAttribute(this),
				new AchievementsAttribute(this), new StatisticsAttribute(this),
				/* new GeoAttribute(this), */
				new CosmeticsAttribute(this), new PetsAttribute(this), new MountsAttribute(this),
				// new TransformationsAttribute(this),
				new GadgetsAttribute(this), new NPCAttribute(this));

		getCommandMap().register("bb", new GameCommands(this, "bb"));
		getCommandMap().register("report", new GameCommands(this, "report"));
	}
	
	public static Game getGame() {
		return game;
	}

}
