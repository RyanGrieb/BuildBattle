package net.mcnations.bb.utils.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import net.mcnations.core.CorePlugin;
import net.mcnations.core.common.enums.GameType;
import net.mcnations.core.database.DatabaseEngine;

public class BattlePlayer
{

	private final UUID uuid;

	private int kills, deaths, wins, losses, draws;
	private List<String> unlockedKits = new ArrayList<>();
	
	public BattlePlayer(UUID uuid)
	{
		this.uuid = uuid;
		
		CorePlugin.getCorePlugin().getScheduler().runTaskAsynchronously(CorePlugin.getCorePlugin(), () -> {
			DatabaseEngine databaseEngine = CorePlugin.getCorePlugin().getDatabaseEngine();
			BasicDBObject searchQuery = new BasicDBObject("uuid", uuid.toString());
			DBCursor result = databaseEngine.findQuery(GameType.BATTLES_ELITE.getDatabaseName(), searchQuery);
			if(result.hasNext())
			{
				this.setKills(databaseEngine.getInteger(result, "kills"));
				this.setDeaths(databaseEngine.getInteger(result, "deaths"));
				this.setWins(databaseEngine.getInteger(result, "wins"));
				this.setLosses(databaseEngine.getInteger(result, "losses"));
				this.setDraws(databaseEngine.getInteger(result, "draws"));
				this.unlockedKits = databaseEngine.getStringList(result, "unlockedKits");
			} else {
				searchQuery.clear();
				searchQuery.put("uuid", uuid.toString());	
				searchQuery.put("kills", 0);	
				searchQuery.put("deaths", 0);	
				searchQuery.put("wins", 0);	
				searchQuery.put("losses", 0);	
				searchQuery.put("unlockedKits", unlockedKits);	
				
				databaseEngine.insertQuery(GameType.BATTLES_ELITE.getDatabaseName(),  searchQuery);
			};
			result.close();
		});
		
	}

	public UUID getUUID() {
		return uuid;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public int getDraws() {
		return draws;
	}

	public void setDraws(int draws) {
		this.draws = draws;
	}
	
	public void addKill()
	{
		this.kills++;
	}
	
	public void addDeath()
	{
		this.deaths++;
	}
	
	public void addWin()
	{
		this.wins++;
	}
	
	public void addLoss()
	{
		this.losses++;
	}
	
	public void addDraw()
	{
		this.draws++;
	}
	
	public void addKit(String newKit)
	{
		this.unlockedKits.add(newKit);
	}
	
	public void save()
	{
		CorePlugin.getCorePlugin().getScheduler().runTaskAsynchronously(CorePlugin.getCorePlugin(), () -> {
			DatabaseEngine databaseEngine = CorePlugin.getCorePlugin().getDatabaseEngine();
			BasicDBObject searchQuery = new BasicDBObject("uuid", uuid.toString());
			DBCursor result = databaseEngine.findQuery(GameType.BATTLES_ELITE.getDatabaseName(), searchQuery);
			if(result.hasNext())
			{
				BasicDBObject updated = new BasicDBObject();
				updated.putAll(result.next());
				
				updated.replace("kills", kills);
				updated.replace("deaths", deaths);
				updated.replace("wins", wins);
				updated.replace("losses", losses);
				updated.replace("draws", draws);
				updated.replace("unlockedKits", unlockedKits);
				
				databaseEngine.updateQuery(GameType.BATTLES_ELITE.getDatabaseName(), searchQuery, updated);
			}
			result.close();
		});
	}
	
	public List<String> getKits() {
		if(unlockedKits == null)
			return unlockedKits = new ArrayList<String>();
		
		return unlockedKits;
	}
	
	public void unlockKit(String string) {
		if (!unlockedKits.contains(string))
			unlockedKits.add(string);
	}
	
}