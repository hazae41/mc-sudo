package hazae41.sudo;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Redirected {
  static Player from(Player target, Player mitm) {
    Player.Spigot spigot = new Player.Spigot() {
      @Override
      public void sendMessage(@NotNull BaseComponent... components) {
        BaseComponent[] prefixed = new ComponentBuilder()
                .append("[" + target.getName() + "] ")
                .append(components)
                .create();

        mitm.spigot().sendMessage(prefixed);
        target.spigot().sendMessage(components);
      }

      @Override
      public void sendMessage(@NotNull BaseComponent component) {
        this.sendMessage(new BaseComponent[]{component});
      }

      @Override
      public void sendMessage(@NotNull ChatMessageType position, @NotNull BaseComponent... components) {
        target.spigot().sendMessage(position, components);
      }

      @Override
      public void sendMessage(@NotNull ChatMessageType position, @Nullable UUID sender, @NotNull BaseComponent... components) {
        target.spigot().sendMessage(position, sender, components);
      }

      @Override
      public void sendMessage(@NotNull ChatMessageType position, @NotNull BaseComponent component) {
        target.spigot().sendMessage(position, component);
      }

      @Override
      public void sendMessage(@NotNull ChatMessageType position, @Nullable UUID sender, @NotNull BaseComponent component) {
        target.spigot().sendMessage(position, sender, component);
      }

      @Override
      public void sendMessage(@Nullable UUID sender, @NotNull BaseComponent component) {
        target.spigot().sendMessage(sender, component);
      }

      @Override
      public void sendMessage(@Nullable UUID sender, @NotNull BaseComponent... components) {
        target.spigot().sendMessage(sender, components);
      }

      @NotNull
      @Override
      public Set<Player> getHiddenPlayers() {
        return target.spigot().getHiddenPlayers();
      }

      @NotNull
      @Override
      public InetSocketAddress getRawAddress() {
        return target.spigot().getRawAddress();
      }

      @Override
      public void respawn() {
        target.spigot().respawn();
      }
    };

    InvocationHandler playerHandler = (proxy, method, args) -> {
      if (method.getName().equals("sendMessage")) {
        if (method.getParameters()[0].getType() == String.class) {
          String message = (String) args[0];
          Objects.requireNonNull(message);
          mitm.sendMessage("[" + target.getName() + "] " + message);
          target.sendMessage(message);
          return null;
        }
      }

      if (method.getName().equals("spigot"))
        return spigot;

      return method.invoke(target, args);
    };

    return (Player) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            playerHandler);
  }
}
