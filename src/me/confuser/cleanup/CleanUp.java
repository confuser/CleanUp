package me.confuser.cleanup;

import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class CleanUp extends JavaPlugin {
	public CleanUp plugin;
	public boolean inProgress = false;
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

	@Override
	public void onDisable() {
		getLogger().info("Completed");
	}

	@Override
	public void onEnable() {
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		plugin = this;

		FileConfiguration config = getConfig();

		essentialsStartup = config.getBoolean("essentials.enabled");
		playersStartup = config.getBoolean("players.enabled");

		essentialsDays = config.getInt("essentials.days");
		playerDays = config.getInt("players.days");

		setLocation = config.getBoolean("players.setLocation.enabled");
		locationWorld = config.getString("players.setLocation.world");
		locationX = config.getDouble("players.setLocation.x");
		locationY = config.getDouble("players.setLocation.y");
		locationZ = config.getDouble("players.setLocation.z");

		boolean disableAll = config.getBoolean("disableAll");

		// getCommand("cleanup").setExecutor(new CleanUpCommand(this));

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

		Clean = new Clean(this);

		getLogger().info("Enabled");

		if (essentialsStartup) {
			getLogger().info("Starting essentials cleanup");
			getLogger().info(Clean.essentials(essentialsDays) + " player yml files deleted");
		}

		if (playersStartup) {
			getLogger().info("Starting player cleanup");
			getLogger().info(Clean.player(playerDays) + " player dat files deleted");
		}

		if (disableAll) {
			getLogger().info("[CleanUp] Disabling startup procedures for future server starts");
			config.set("essentials.enabled", false);
			config.set("players.enabled", false);
			config.set("players.setLocation.enabled", false);

			this.saveConfig();
		}

		plugin.getPluginLoader().disablePlugin(this);

	}
}
