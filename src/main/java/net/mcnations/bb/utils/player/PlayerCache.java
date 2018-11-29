package net.mcnations.bb.utils.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCache
{

	private static Map<UUID, BattlePlayer> cachedPlayers = new HashMap<>();

	public static void cachePlayer(UUID uuid, BattlePlayer battlePlayer)
	{
		BattlePlayer received = cachedPlayers.get(uuid);
		if(received == null)
			cachedPlayers.put(uuid, battlePlayer);
	}
	
	public static boolean isPlayerCached(UUID uuid)
	{
		BattlePlayer received = cachedPlayers.get(uuid);
		if(received == null)
			return false;
		
		return true;
	}
	
	public static void removePlayerCache(UUID uuid)
	{
		BattlePlayer received = cachedPlayers.get(uuid);
		if(received != null)
		{
			received.save();
			cachedPlayers.remove(uuid);
		}
	}
	
	public static BattlePlayer getPlayerCache(UUID uuid)
	{
		return cachedPlayers.get(uuid);
	}
	
}
