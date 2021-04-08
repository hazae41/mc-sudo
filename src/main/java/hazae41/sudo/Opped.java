package hazae41.sudo;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Opped {
  static Player from(Player target, boolean allPerms) {
    InvocationHandler handler = (proxy, method, args) -> {
      if (method.getName().equals("isOp"))
        return true;

      if (method.getName().equals("hasPermission"))
        if (allPerms) return true;

      return method.invoke(target, args);
    };

    return (Player) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            handler);
  }

  static CommandSender from(CommandSender target, boolean allPerms) {
    InvocationHandler handler = (proxy, method, args) -> {
      if (method.getName().equals("isOp"))
        return true;

      if (method.getName().equals("hasPermission"))
        if (allPerms) return true;

      return method.invoke(target, args);
    };

    return (CommandSender) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            handler);
  }
}