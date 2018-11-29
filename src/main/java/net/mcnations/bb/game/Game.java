package net.mcnations.bb.game;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import net.mcnations.bb.BuildBattle;
import net.mcnations.bb.game.states.InGame;
import net.mcnations.bb.game.states.InJudge;
import net.mcnations.bb.game.states.InTiebreaker;
import net.mcnations.bb.game.states.Lobby;
import net.mcnations.core.CorePlugin;
import net.mcnations.core.engine.GameEngine;
import net.mcnations.core.engine.GameState;
import net.mcnations.core.engine.logger.GameLogger;

public class Game extends GameEngine
{

	private final Location lobby = new Location(Bukkit.getWorld("hubWorld"), 0, 65, 0);
	
	private List<GameState> allStates = new ArrayList<>();
    private List<UUID> gamePlayers = new ArrayList<>();
    private List<UUID> gameSpectators = new ArrayList<>();
    private List<UUID> joinedSpectators = new ArrayList<>();
	
	public Game(GameLogger logger, String displayName, Plugin plugin, CorePlugin corePlugin) 
	{
		super(logger, displayName, plugin, corePlugin);
		
		allStates.add(new Lobby(this, "IN_LOBBY", "Lobby"));
		allStates.add(new InGame(this, "IN_GAME", "Game"));
		allStates.add(new InJudge(this, "IN_JUDGE", "Judge"));
		allStates.add(new InTiebreaker(this, "IN_TIEBREAKER", "Tiebreaker"));
	}

	@Override
	public List<GameState> getAllStates() 
	{
		return allStates;
	}

	@Override
	public List<UUID> getGamePlayers() 
	{
		return gamePlayers;
	}

	@Override
	public List<UUID> getGameSpectators()
	{
		return gameSpectators;
	}

	@Override
	public List<UUID> getJoinedSpectators()
	{
		return joinedSpectators;
	}

	@Override
	public int getGameMaxPlayers()
	{
		return BuildBattle.getCorePlugin().getConfig().getInt("MaxPlayers");
	}

	@Override
	public int getGameMinPlayers() 
	{
		return BuildBattle.getCorePlugin().getConfig().getInt("MaxPlayersReady");
	}

	@Override
	public int getMaxJoinedSpectators() 
	{
		return 0;
	}

	@Override
	public Location getLobbyLocation() 
	{
		return lobby;
	}
	
	public static String formatInteger(int i)
    {
        return NumberFormat.getNumberInstance(Locale.UK).format(i);
    }

}