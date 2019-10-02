package hazae41.minecraft.sudo.bukkit

import hazae41.minecraft.kutils.textOf
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

class RedirectedPlayer(val sub: Player, val target: Player) : Player by target {
    override fun sendMessage(message: String?) = sub.sendMessage("[${target.name}] $message")
    override fun spigot() = spigot
    
    val spigot = object : Player.Spigot() {
        override fun sendMessage(component: BaseComponent?) {
            sub.spigot().sendMessage(textOf("[${target.name}] ").apply { addExtra(component) })
        }
    }
}

class OppedPlayer(val sub: Player, val allPerms: Boolean = true) : Player by sub {
    override fun isOp() = true
    override fun hasPermission(name: String?) = allPerms || sub.hasPermission(name)
    override fun hasPermission(perm: Permission?) = allPerms || sub.hasPermission(perm)
}

class OppedSender(val sub: CommandSender, val allPerms: Boolean = true) : CommandSender by sub {
    override fun isOp() = true
    override fun hasPermission(name: String?) = allPerms || sub.hasPermission(name)
    override fun hasPermission(perm: Permission?) = allPerms || sub.hasPermission(perm)
}