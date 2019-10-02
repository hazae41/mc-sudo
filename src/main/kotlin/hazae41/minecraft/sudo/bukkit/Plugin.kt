package hazae41.minecraft.sudo.bukkit

import hazae41.minecraft.kutils.bukkit.BukkitPlayer
import hazae41.minecraft.kutils.bukkit.BukkitPlugin
import hazae41.minecraft.kutils.bukkit.PluginConfigFile
import hazae41.minecraft.kutils.bukkit.init
import hazae41.minecraft.kutils.bukkit.logToFile
import hazae41.minecraft.kutils.bukkit.severe
import hazae41.minecraft.kutils.bukkit.update
import hazae41.minecraft.kutils.catch
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Plugin : BukkitPlugin() {
    override fun onEnable() = catch(::err) {
        update(62819)
        init(Data)
        makeCommands()
        makeListeners()
    }
}

fun Plugin.err(ex: Exception) {
    severe(ex); logToFile(ex)
}

fun Player.asOp(allPerms: Boolean = false) = OppedPlayer(this, allPerms)
fun CommandSender.asOp(allPerms: Boolean = false) = OppedSender(this, allPerms)
fun Player.asPlayer(target: Player) = RedirectedPlayer(this, target)

object Data : PluginConfigFile("data") {
    class Player(player: BukkitPlayer) {
        var target by string(player.uniqueId.toString())
    }
}

val Player.data
    get() = Data.Player(this)

fun Player.exit() {
    data.target = ""
}