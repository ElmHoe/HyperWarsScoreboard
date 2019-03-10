package uk.co.ElmHoe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import uk.co.ElmHoe.Data;
import uk.co.ElmHoe.Utilities.PlayerUtility;
import uk.co.ElmHoe.Utilities.StringUtility;
import uk.co.ElmHoe.InfoboardAPI.InfoboardAPI;

public class PlayerData {
	
	private UUID uuid;
	private int kills;
	private int deaths;
	private int wins;
	private int loses;
	private int games;
	private int score;
	
	public PlayerData(UUID uuid){
		this.uuid = uuid;
		kills = 0;
		deaths = 0;
		wins = 0;
		loses = 0;
		games = 0;
		score = 0;
		this.updateData();
	}
	
	public void updateScoreboard(){
		if(PlayerUtility.isOnline(uuid) && HyperWarsScoreboard.scoreboardSlot != -1){
			Map<String, Integer> data = new HashMap<String, Integer>();
			for(String line : HyperWarsScoreboard.scoreboardData){
				String split[] = line.split(":");
				data.put(StringUtility.trim(split[0].
						replace("{PLAYER}", Bukkit.getPlayer(uuid).getName()).
						replace("{KILLS}", "" + kills).
						replace("{DEATHS}", "" + deaths).
						replace("{ONLINE}", "" + PlayerUtility.getOnlinePlayers().size()).
						replace("{WINS}", "" + wins).
						replace("{GAMES}", "" + games).
						replace("{SCORE}", "" + score).
						replace("{LOSES}", "" + loses), 16), Integer.parseInt(split[1]));
			}
			InfoboardAPI.getPlayer(uuid).setData(HyperWarsScoreboard.scoreboardSlot, HyperWarsScoreboard.scoreboardTitle, data);
		}
	}
	
	public void updateData(){
		int killsToSet = 0;
		int deathsToSet = 0;
		int winsToSet = 0;
		int losesToSet = 0;
		int scoreToSet = 0;
		int gamesToSet = 0;
		if(Data.getConfig().contains(uuid.toString() + ".cellwars")){
			winsToSet = Data.getConfig().getInt(uuid.toString() + ".cellwars.wins");
			gamesToSet = Data.getConfig().getInt(uuid.toString() + ".cellwars.games");
			scoreToSet = Data.getConfig().getInt(uuid.toString() + ".cellwars.score");
			killsToSet = Data.getConfig().getInt(uuid.toString() + ".cellwars.kills");
			deathsToSet = Data.getConfig().getInt(uuid.toString() + ".cellwars.deaths");
			losesToSet = gamesToSet - winsToSet;
		}
		kills = killsToSet;
		deaths = deathsToSet;
		wins = winsToSet;
		loses = losesToSet;
		score = scoreToSet;
		games = gamesToSet;
		if(PlayerUtility.isOnline(uuid)){
			this.updateScoreboard();
			//TODO: Pex references for prefixes again...
			//PexUtility.resetPrefix(uuid);
			//PexUtility.setPrefix(uuid, HyperWarsScoreboard.prefix.replace("{SCORE}", "" + score) + PexUtility.getPrefix(uuid));
		}
	}
	
	public UUID getUUID(){return uuid;}
}
