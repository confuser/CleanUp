package me.confuser.cleanup;

import java.io.File;
import java.io.IOException;

import me.confuser.offlineplayer.OfflinePlayerFile;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

public class Clean {
	private CleanUp plugin;

	Clean(CleanUp instance) {
		plugin = instance;
	}

	public int essentials(int days) {
		int playersDeleted = 0;

		Long timeNow = System.currentTimeMillis();
		Long dayToMs = (long) (86400000 * days);
		YamlConfiguration config;
		Long logout;
		Long difference;

		File[] essentials = new File("plugins/Essentials/userdata").listFiles();

		for (File f : essentials) {
			logout = null;
			difference = null;

			config = YamlConfiguration.loadConfiguration(new File(f.getAbsolutePath()));
			logout = config.getLong("timestamps.logout");

			if (logout != null && logout != 0) {
				difference = timeNow - logout;
				if (difference >= dayToMs) {
					f.delete();
					playersDeleted++;
				}
			}
		}

		return playersDeleted;
	}

	public int player(int days) {
		int playersDeleted = 0;
		Long dayToMs = (long) (86400000 * days);

		for (OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
			OfflinePlayerFile offlinePlayer;
			try {
				offlinePlayer = new OfflinePlayerFile(player.getName());
			} catch (IOException e) {
				continue;
			}

			if (offlinePlayer.getNbt() == null)
				continue;

			Long difference = System.currentTimeMillis() - offlinePlayer.getLastSeen();

			if (days != 0 && difference >= dayToMs) {
				// Delete .dat file!
				File file = new File("world/players/" + player.getName() + ".dat");
				if (file.exists()) {
					file.delete();
					playersDeleted++;
				}
			} else if (plugin.setLocation) {
				offlinePlayer.setLocation(plugin.getServer().getWorld(plugin.locationWorld), plugin.locationX, plugin.locationY, plugin.locationZ);
				playersDeleted++;
			}
		}
		return playersDeleted;
	}

}
