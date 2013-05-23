package me.confuserr.cleanup;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CleanUp extends JavaPlugin {
	public Logger logger = Logger.getLogger("Minecraft");
	public CleanUp plugin;
	public boolean inProgress = false;
	boolean pexStartup;
	boolean essentialsStartup;
	boolean playersStartup;
	public Clean Clean;
	public int essentialsDays;
	public int playerDays;
	public boolean setLocation;
	public String locationWorld;
	public double locationX;
	public double locationY;
	public double locationZ;
	public String pexFile;
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("["+pdfFile.getName() + "] has been disabled");
	}

	@Override
	public void onEnable() {
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		plugin = this;
		
		FileConfiguration config = getConfig();
		
		pexStartup = config.getBoolean("pex.enabled");
		essentialsStartup = config.getBoolean("essentials.enabled");
		playersStartup = config.getBoolean("players.enabled");
		
		essentialsDays = config.getInt("essentials.days");
		playerDays = config.getInt("players.days");
		
		pexFile = config.getString("pex.file");
		
		setLocation = config.getBoolean("players.setLocation.enabled");
		locationWorld = config.getString("players.setLocation.world");
		locationX = config.getDouble("players.setLocation.x");
		locationY = config.getDouble("players.setLocation.y");
		locationZ = config.getDouble("players.setLocation.z");
		
		boolean disableAll = config.getBoolean("disableAll");
		
		PluginDescriptionFile pdfFile = this.getDescription();
		
		//getCommand("cleanup").setExecutor(new CleanUpCommand(this));
		
		try {
		    MetricsLite metrics = new MetricsLite(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
		
		Clean = new Clean(this);
		
		this.logger.info("["+pdfFile.getName()+"] Version:" + pdfFile.getVersion() + " has been enabled");
		
		if(pexStartup) {
			this.logger.info("Starting pex cleanup");
			this.logger.info(Clean.pex()+" nodes cleaned in pex");
		}
		
		if(essentialsStartup) {
			this.logger.info("Starting essentials cleanup");
			this.logger.info(Clean.essentials(essentialsDays)+" player yml files deleted");
		}
		
		if(playersStartup) {
			this.logger.info("Starting player cleanup");
			this.logger.info(Clean.player(playerDays)+" player dat files deleted");
		}
		
		if(disableAll) {
			this.logger.info("[CleanUp] Disabling startup procedures for future server starts");
			config.set("pex.enabled", false);
			config.set("essentials.enabled", false);
			config.set("players.enabled", false);
			config.set("players.setLocation.enabled", false);
			
			this.saveConfig();
		}
		
		plugin.getPluginLoader().disablePlugin(this);
		
	}
}
