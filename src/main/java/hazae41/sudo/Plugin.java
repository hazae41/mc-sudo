package hazae41.sudo;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class Plugin extends JavaPlugin implements Listener {
  PluginConfig playersConfig = new PluginConfig(this, "players.yml");
  Map<OfflinePlayer, OfflinePlayer> targets = new HashMap<>();

  @Override
  public void onEnable() {
    playersConfig.saveDefaultConfig();

    getServer().getPluginManager().registerEvents(this, this);

    loadTargets();
  }

  void loadTargets() {
    Configuration config = playersConfig.getConfig();

    for (String key : config.getKeys(false)) {
      String value = config.getString(key);
      if (value == null || value.isEmpty()) continue;
      OfflinePlayer player = getServer()
              .getOfflinePlayer(UUID.fromString(key));
      OfflinePlayer target = getServer()
              .getOfflinePlayer(UUID.fromString(value));
      targets.put(player, target);
    }
  }

  @Nullable OfflinePlayer getTarget(Player player) {
    return targets.get(player);
  }

  void setTarget(OfflinePlayer player, @Nullable OfflinePlayer target) {
    if (target == null) targets.remove(player);
    else targets.put(player, target);

    Configuration config = playersConfig.getConfig();
    String key = player.getUniqueId().toString();

    if (target == null) config.set(key, null);
    else config.set(key, target.getUniqueId().toString());

    playersConfig.save();
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCommandEvent(PlayerCommandPreprocessEvent e) {
    OfflinePlayer target = getTarget(e.getPlayer());
    if (target == null) return;

    e.setCancelled(true);

    if (e.getMessage().equals("/su")) {
      setTarget(e.getPlayer(), null);
      e.getPlayer().sendMessage(ChatColor.BLUE + "Exited sudo of " + target.getName());
      return;
    }

    if (!target.isOnline()) {
      String name = Objects.requireNonNull(target.getName());
      e.getPlayer().sendMessage(ChatColor.RED + name + " is not online");
      return;
    }

    Player online = Objects.requireNonNull(target.getPlayer());
    Player redirected = Redirected.from(online, e.getPlayer());

    try {
      boolean result = getServer().dispatchCommand(redirected, e.getMessage().substring(1));
      if (!result) e.getPlayer().sendMessage(ChatColor.RED + "Unknown sudoed command");
    } catch (CommandException ex) {
      if (ex.getCause() instanceof ClassCastException)
        e.getPlayer().sendMessage(ChatColor.RED + "This command is incompatible with sudo");
      else throw ex;
    }
  }

  static class Message extends Throwable {
    public Message(String message) {
      super(message);
    }
  }
  
  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
    if (command.getName().equals("sudo") || command.getName().equals("asop"))
      return Collections.emptyList();

    if (command.getName().equals("su"))
      return getServer().getOnlinePlayers().stream()
              .map(Player::getName).collect(Collectors.toList());

    return Collections.emptyList();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    try {
      if (command.getName().equals("sudo") || command.getName().equals("asop")) {
        String subcommand = String.join(" ", args);
        if (subcommand.isEmpty()) return false;

        boolean allPerms = command.getName().equals("sudo");

        CommandSender opped = sender instanceof Player
                ? Opped.from((Player) sender, allPerms)
                : Opped.from(sender, allPerms);

        try {
          boolean result = getServer().dispatchCommand(opped, subcommand);
          if (!result) throw new Message("Unknown sudoed command");
        } catch (CommandException ex) {
          if (ex.getCause() instanceof ClassCastException)
            throw new Message("This command is incompatible with sudo");
          else throw ex;
        }

        return true;
      }

      if (command.getName().equals("su")) {
        if (!(sender instanceof Player))
          throw new Message("You are not a player");
        Player player = (Player) sender;

        String name = Utils.get(args, 0).orElse(null);
        if (name == null) return false;

        List<Player> matches = getServer().matchPlayer(name);
        Player target = Utils.get(matches, 0).orElse(null);
        if (target == null) throw new Message("Player not found");

        setTarget(player, target);
        player.sendMessage(ChatColor.BLUE + "Now executing commands as " + target.getName());
        return true;
      }

      return false;
    } catch (Message msg) {
      sender.sendMessage(ChatColor.RED + msg.getMessage());
      return true;
    }
  }
}
