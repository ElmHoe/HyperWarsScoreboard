package uk.co.ElmHoe.CellWarsScoreboard;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.ElmHoe.Utilities.PlayerUtility;
import uk.co.ElmHoe.Utilities.StringUtility;
import uk.co.ElmHoe.CellWars.ArenaUpdateEvent;
import uk.co.ElmHoe.CellWars.CellWars;
import uk.co.ElmHoe.CellWarsScoreboard.PlayerData;

public class CellWarsScoreboard extends JavaPlugin implements Listener{
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onArenaUpdate(ArenaUpdateEvent event) {
		for(PlayerData player : playerData){
			if(!CellWars.plugin.isSpectating(player.getUUID())){
				player.updateData();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(getFromUUID(event.getPlayer().getUniqueId()) == null){
			playerData.add(new PlayerData(event.getPlayer().getUniqueId()));
		}
		for(PlayerData player : playerData){
			player.updateScoreboard();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(final PlayerKickEvent event) {
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
		    @Override
		    public void run() {
		    	for(PlayerData player : playerData){
					if(!player.getUUID().equals(event.getPlayer().getUniqueId())){
						player.updateScoreboard();
					}
				}
		    	removeOffline();
		    }
		}, 1);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
		    @Override
		    public void run() {
		    	for(PlayerData player : playerData){
					if(!player.getUUID().equals(event.getPlayer().getUniqueId())){
						player.updateScoreboard();
					}
				}
		    	removeOffline();
		    }
		}, 1);
	}
	
	File configFile;
	FileConfiguration config;
	public static List<String> scoreboardData = new ArrayList<String>();
	public static List<PlayerData> playerData = new ArrayList<PlayerData>();
	List<PlayerData> allData = new ArrayList<PlayerData>();
	public static String scoreboardTitle;
	public static int scoreboardSlot;
	public static String prefix;
	
	
	public static PlayerData getFromUUID(UUID uuid){
		for(PlayerData player : playerData){
			if(player.getUUID().equals(uuid)){
				return player;
			}
		}
		return null;
	}
	
	public void removeOffline(){
		for(int i = 0; i < playerData.size(); i++){
			if(!PlayerUtility.isOnline(playerData.get(i).getUUID())){
				playerData.remove(i);
			}
		}
	}
	
	@Override
    public void onEnable(){
		Bukkit.getConsoleSender().sendMessage("CellWarsScoreboard Loading Please Wait....");
		configFile = new File(getDataFolder(), "config.yml");
		try{
			firstRun();
		} catch (Exception e){
			e.printStackTrace();
		}
		config = new YamlConfiguration();
	    loadYamls();
		init();
		Bukkit.getServer().getPluginManager().registerEvents(this,this);
		Bukkit.getConsoleSender().sendMessage("CellWarsScoreboard Version 1.0 by HctiMitcH");
    }
	
	private void init(){
		scoreboardTitle = StringUtility.format(config.getString("config.Infoboard.title"));
		scoreboardData = StringUtility.format(config.getStringList("config.Infoboard.data"));
		scoreboardSlot = config.getInt("config.Infoboard.scoreboardSlot");
		prefix = StringUtility.format(config.getString("config.prefix"));
	}
	
	private void firstRun() throws Exception {
	    if(!configFile.exists()){
	        configFile.getParentFile().mkdirs();
	        copy(getResource("config.yml"), configFile);
	    }
	}
	
	private void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void loadYamls() {
	    try {
	        config.load(configFile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
