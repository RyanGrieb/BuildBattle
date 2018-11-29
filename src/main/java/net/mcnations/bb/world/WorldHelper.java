package net.mcnations.bb.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.mcnations.bb.BuildBattle;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Monster;

public class WorldHelper {
	
	public static String mapName = BuildBattle.getCorePlugin().getConfig().getString("Map.name");
	public static String mapAuthor = BuildBattle.getCorePlugin().getConfig().getString("Map.author");
   
	 public static String gameworld = "playWorld";

	    public static void loadWorld()
	    {
	        try
	        {
	            _loadMap(gameworld, false);
	        } catch (IOException e)
	        {
	            e.printStackTrace();
	        }

	        new BukkitRunnable()
	        {
	            @Override
	            public void run()
	            {
	                World world = Bukkit.getWorld(gameworld);
	                if (world == null)
	                {
	                    WorldCreator creator = new WorldCreator(gameworld);
	                    creator.environment(World.Environment.NORMAL);
	                    creator.generateStructures(true);
	                    world = creator.createWorld();
	                    world.setAutoSave(false);
	                    world.setTime(0);
	                    world.setStorm(false);
	                    world.setWeatherDuration(9999);

	                    BuildBattle.getCorePlugin().getLogger().info(gameworld + " generated");
	                }
	            }
	        }.runTaskLater( BuildBattle.getCorePlugin(), 2 * 20L);
	    }

	    private static String _loadMap(String mapName, boolean fallback) throws IOException
	    {
	    	 BuildBattle.getCorePlugin().getLogger().info("Copying template to game world.");
	        File templateWorld = new File("gamefiles/" + mapName + "/");
	        if (!templateWorld.exists() || !templateWorld.isDirectory())
	        {
	        	 BuildBattle.getCorePlugin().getLogger().severe("Template world is not a directory or does not exist.");
	            return null;
	        }

	        String rollback = gameworld;
	        File gameWorld = new File(rollback);
	        if (gameWorld.exists())
	        {
	            try
	            {
	                FileUtils.deleteDirectory(gameWorld);
	                if (gameWorld.exists() && gameWorld.isDirectory())
	                {
	                    throw new IOException("gameWorld directory was not deleted.");
	                }
	            } catch (IOException e)
	            {
	            	 BuildBattle.getCorePlugin().getLogger().log(Level.SEVERE, "Unable to delete game world directory", e);
	                if (fallback)
	                {
	                	 BuildBattle.getCorePlugin().getLogger().warning("Failed fallback maps load: stopping");
	                    return null;
	                } else
	                {
	                    return _loadMap(mapName, true);
	                }
	            }
	        }

	        try
	        {
	            FileUtils.copyDirectory(templateWorld, gameWorld);
	        } catch (IOException e)
	        {
	        	 BuildBattle.getCorePlugin().getLogger().log(Level.SEVERE, "Unable to copy template to game world", e);
	            return null;
	        }

	        BuildBattle.getCorePlugin().getLogger().info("Finished copying template to game world.");
	        return rollback;
	    }

	    public static void deleteWorld(boolean beforeMap)
	    {
	        final World gameWorld = Bukkit.getWorld(gameworld);
	        if (gameWorld != null)
	        {
	            if (Bukkit.unloadWorld(gameWorld, false))
	            {
	                BuildBattle.getCorePlugin().getLogger().info(gameWorld.getName() + " successfully unloaded.");
	                if (!beforeMap)
	                	 BuildBattle.getCorePlugin().getServer().getScheduler().runTaskLaterAsynchronously(BuildBattle.getCorePlugin(), new Runnable()
	                    {
	                        private int tries = 0;

	                        @Override
	                        public void run()
	                        {
	                            try
	                            {
	                                FileUtils.deleteDirectory(new File(gameWorld.getName()));
	                                BuildBattle.getCorePlugin().getLogger().info("World folder deleted");
	                            } catch (IOException e)
	                            {
	                            	 BuildBattle.getCorePlugin().getLogger().log(Level.SEVERE, "Unable to delete world directory (try: " + tries + ")", e);
	                                if (tries < 2)
	                                {
	                                    tries++;
	                                    BuildBattle.getCorePlugin().getServer().getScheduler().runTaskLaterAsynchronously( BuildBattle.getCorePlugin(), this, (tries + 1) * 20L);
	                                }
	                            }
	                        }
	                    }, 5L);
	            } else
	            {
	            	 BuildBattle.getCorePlugin().getLogger().severe("Unable to unload world" + gameWorld.getName());
	            }
	        }
	    }
	
    
	public static World getNether()
	{
	return Bukkit.getServer().getWorld("hubWorld_nether");
	}
	
	public static World getHubWorld()
	{
	return Bukkit.getServer().getWorlds().get(0);
	}
	
	public static World getPlayWorld()
	{
	return Bukkit.getServer().getWorld("playWorld");
	}
	
	public static boolean checkIfCorrupt()
	{
		//Implement this somewhere...
		File target = new File("playWorld\\region");
		File nonCorruptFile = new File("playWorld\\data");
		if(target.exists() && !nonCorruptFile.exists())
		{
			return true;
		}
		else
			return false;
	}
	
	public static void setAlwaysDay(World w)
	{
		w.setTime(1100);
		w.setStorm(false);
		w.setWeatherDuration(9999);
		w.setMonsterSpawnLimit(0);
		for(Entity entity : w.getEntities()) {
			
		if(entity instanceof Monster)
			entity.remove();
		}
	}

	public static Location getHubLocation() {
		int x = BuildBattle.getCorePlugin().getConfig().getInt("Hub.x");
		int y = BuildBattle.getCorePlugin().getConfig().getInt("Hub.y");
		int z = BuildBattle.getCorePlugin().getConfig().getInt("Hub.z");
		return new Location(WorldHelper.getHubWorld(),x,y,z);
	}

	public static Location getCenterPlot() {
		int x = BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.x");
		int y = BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.y");
		int z = BuildBattle.getCorePlugin().getConfig().getInt("TopLeftPlot.z");
		return new Location(WorldHelper.getPlayWorld(),x,y,z);
	}

	public static void resetPlotSettings(Player player) {
		player.resetPlayerTime();
		PlotHelper.resetTime();
		
		player.resetPlayerWeather();
		player.getWorld().setStorm(false);
		PlotHelper.resetPlotWeather();
		
		
	}
	
	public static void setPlayerWeather(Player player, String str)
	{
		switch(str)
		{
		case "clear":
			player.setPlayerWeather(WeatherType.CLEAR);
			break;
			
		case "rain":
			player.setPlayerWeather(WeatherType.DOWNFALL);
			break;
		
		case "thunderstorm":
			player.setPlayerWeather(WeatherType.DOWNFALL);
			break;
		}
	}
	
}
