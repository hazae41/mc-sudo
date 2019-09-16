@file:JvmName("SudoBukkit")

package hazae41.minecraft.sudo.bukkit

import hazae41.minecraft.kotlin.bukkit.*
import hazae41.minecraft.kotlin.catch
import hazae41.minecraft.kotlin.lowerCase
import hazae41.minecraft.kotlin.not
import hazae41.minecraft.kotlin.textOf
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.permissions.Permission

class SudoBukkitPlugin: BukkitPlugin(){

    fun err(ex: Exception){ severe(ex); logToFile(ex) }

    override fun onEnable() = catch(::err){
        update(62819)
        init(Data)

        command("sudo"){ args ->
            args.getOrNull(0)
                ?: return@command msg("&cUsage: /sudo <command>")

            val cmd = args.joinToString(" ")
            val opped = (this as? Player)?.asOp(true) ?: asOp(true)

            dispatchCommand(opped, cmd)
        }

        command("asop"){ args ->
            args.getOrNull(0)
                ?: return@command msg("&cUsage: /asop <command>")

            val cmd = args.joinToString(" ")
            val opped = (this as? Player)?.asOp() ?: asOp()

            dispatchCommand(opped, cmd)
        }

        command("su"){ args ->
            if(this !is Player)
                return@command msg("&cYou're not a player")

            val arg = args.getOrNull(0)
                ?: return@command msg("&cUsage: /su <target>")

            val matches = matchPlayer(arg)
            val target = matches.getOrNull(0)
                ?: return@command msg("&cUnknown player")

            Data.Player(this).target = target.name
            msg("&bNow executing Bukkit commands as ${target.name}")
        }

        listen<PlayerCommandPreprocessEvent>(LOW) {
            val data = Data.Player(it.player).target.not("") ?: return@listen

            it.isCancelled = true

            if(it.message.lowerCase == "/su")
                return@listen it.player.exit()

            val target = getPlayer(data)
                ?: return@listen it.player.msg("&c$data is not online")

            dispatchCommand(RedirectedPlayer(it.player, target), it.message.drop(1))
        }
    }
}

object Data: PluginConfigFile("data"){
    class Player(player: BukkitPlayer){
        var target by string(player.uniqueId.toString())
    }
}

fun Player.exit() {
    Data.Player(this).target = ""
    msg("&bExited Bukkit su")
}

fun Player.asOp(allPerms: Boolean = false) = OppedPlayer(this, allPerms)
fun CommandSender.asOp(allPerms: Boolean = false) = OppedSender(this, allPerms)

class RedirectedPlayer(val sub: Player, val target: Player): Player by target{
    override fun sendMessage(message: String?) = sub.sendMessage("[${target.name}] $message")
    override fun spigot() = spigot
    val spigot = object: Player.Spigot(){
        override fun sendMessage(component: BaseComponent?) {
            sub.spigot().sendMessage(textOf("[${target.name}] ").apply{addExtra(component)})
        }
    }
}

class OppedPlayer(val sub: Player, val allPerms: Boolean = true): Player by sub{
    override fun isOp() = true
    override fun hasPermission(name: String?) = allPerms || sub.hasPermission(name)
    override fun hasPermission(perm: Permission?) = allPerms || sub.hasPermission(perm)
}

class OppedSender(val sub: CommandSender, val allPerms: Boolean = true): CommandSender by sub{
    override fun isOp() = true
    override fun hasPermission(name: String?) = allPerms || sub.hasPermission(name)
    override fun hasPermission(perm: Permission?) = allPerms || sub.hasPermission(perm)
}