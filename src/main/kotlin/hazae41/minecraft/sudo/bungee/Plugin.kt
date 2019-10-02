package hazae41.minecraft.sudo.bungee

import hazae41.minecraft.kutils.bungee.BungeePlugin
import hazae41.minecraft.kutils.bungee.PluginConfigFile
import hazae41.minecraft.kutils.bungee.init
import hazae41.minecraft.kutils.bungee.logToFile
import hazae41.minecraft.kutils.bungee.severe
import hazae41.minecraft.kutils.bungee.update
import hazae41.minecraft.kutils.catch
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer

class Plugin : BungeePlugin() {
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

fun ProxiedPlayer.asOp() = OppedPlayer(this)
fun CommandSender.asOp() = OppedSender(this)
fun ProxiedPlayer.asPlayer(target: ProxiedPlayer) = RedirectedPlayer(this, target)

object Data : PluginConfigFile("data") {
    class Player(player: ProxiedPlayer) {
        var target by string(player.uniqueId.toString())
    }
}

val ProxiedPlayer.data
    get() = Data.Player(this)

fun ProxiedPlayer.exit() {
    data.target = ""
}



