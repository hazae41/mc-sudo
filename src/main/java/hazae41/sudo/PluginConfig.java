package hazae41.sudo;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class PluginConfig {
  private final JavaPlugin plugin;
  private final String path;
  private final File file;
  private FileConfiguration config;

  public PluginConfig(JavaPlugin plugin, String path) {
    this.plugin = plugin;
    this.path = path;

    this.file = new File(plugin.getDataFolder(), path);
  }

  public File getFile() {
    return file;
  }

  public FileConfiguration getConfig() {
    if (config != null) return config;
    return this.config = YamlConfiguration.loadConfiguration(file);
  }

  public void saveDefaultConfig() {
    plugin.saveResource(path, false);
  }

  public void save() {
    try {
      config.save(file);
    } catch (IOException ex) {
      plugin.getLogger().log(Level.SEVERE, "Could not save config to " + file, ex);
    }
  }
}
