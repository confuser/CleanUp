package me.confuserr.cleanup;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.one4me.ImprovedOfflinePlayer;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Clean {
	private CleanUp plugin;
	
	Clean(CleanUp instance ) {
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
		
		
		for(File f : essentials) {
			logout = null;
			difference = null;
			
			config = YamlConfiguration.loadConfiguration(new File(f.getAbsolutePath()));
			logout = config.getLong("timestamps.logout");
			
			if(logout != null && logout != 0) {
				difference = timeNow - logout;
				if(difference >= dayToMs) {
					f.delete();
					playersDeleted++;
				}
			}
		}
		
		return playersDeleted;
	}
	
	public int player(int days) {
		int playersDeleted = 0;
		OfflinePlayer[] users = plugin.getServer().getOfflinePlayers();
		Location location = null;
		if(plugin.setLocation)
			location = new Location(plugin.getServer().getWorld(plugin.locationWorld), plugin.locationX, plugin.locationY, plugin.locationZ);
		for(OfflinePlayer u : users) {
			String username = u.getName();
			Long difference = System.currentTimeMillis() - u.getLastPlayed();
			Long dayToMs = (long) (86400000 * days);
			if(difference >= dayToMs) {
				// Delete .dat file!
				File file = new File("world/players/"+username+".dat");
				if(file.exists()) {
					file.delete();
					playersDeleted++;
				}
			} else if(plugin.setLocation) {
				ImprovedOfflinePlayer iop = new ImprovedOfflinePlayer(username);
				if(iop.exists())
					iop.setMiscLocation(location);
			}
		}
		return playersDeleted;
	}
	
	public int pex() {
		int cleaned = 0;
		PermissionManager pex = PermissionsEx.getPermissionManager();
		YamlConfiguration file = YamlConfiguration.loadConfiguration(new File("plugins/PermissionsEx/"+plugin.pexFile));
		PermissionUser[] users = pex.getUsers();
		for( PermissionUser u : users ) {
			String username = u.getName();
			String path = "users."+username;
			
			List<String> group = file.getStringList(path+".group");
			ConfigurationSection worlds = file.getConfigurationSection(path+".worlds");
			if(worlds != null) {
				Set<String> availableWorlds = worlds.getValues(true).keySet();
				int worldCount = availableWorlds.size();
				int found = 0;
				Iterator<String> it = availableWorlds.iterator();
				while(it.hasNext()) {
					String next = it.next();
					if(!next.contains(".permissions")) {
						if(file.getStringList(path+".worlds."+next+".permissions").size() == 0) {
							file.set(path+".worlds."+next, null);
							cleaned++;
							found++;
						}
					}
				}
				if(found == worldCount) {
					file.set(path+".worlds", null);
					cleaned++;
				}
			}
			if(group.size() == 0) {
				file.set("users."+u.getName()+".group", null);
				cleaned++;
			}
			
			ConfigurationSection ul = file.getConfigurationSection(path);
			if(ul.getValues(true).keySet().size() == 0) {
				file.set(path, null);
				cleaned++;
			}
		}
		try {
			file.save("plugins/PermissionsEx/"+plugin.pexFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cleaned;
	}
}
