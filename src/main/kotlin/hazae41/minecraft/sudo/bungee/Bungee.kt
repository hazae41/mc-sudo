package hazae41.minecraft.sudo.bungee

import hazae41.minecraft.kutils.*
import hazae41.minecraft.kutils.bungee.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.event.EventPriority.LOW

class SudoBungeePlugin: BungeePlugin(){

    fun err(ex: Exception){ severe(ex); logToFile(ex) }

    override fun onEnable() = catch(::err){
        update(62819)
        init(Data)
        command("gsudo", "sudo.sudo"){ args ->
            args.getOrNull(0)
                ?: return@command msg("&cUsage: /gsudo <command>")

            val cmd = args.joinToString(" ")
            val opped = (this as? ProxiedPlayer)?.asOp() ?: asOp()

            proxy.pluginManager.dispatchCommand(opped, cmd).also {
                success -> if(!success) msg("&cUnknown command")
            }
        }
        command("gsu", "sudo.su"){ args ->
            if(this !is ProxiedPlayer)
                return@command msg("&cYou're not a player")

            val arg = args.getOrNull(0)
                ?: return@command msg("&cUsage: /gsu <target>")

            val matches = proxy.matchPlayer(arg)
            val target = matches.toList().getOrNull(0)
                ?: return@command msg("&cUnknown player")

            Data.Player(this).target = target.name
            msg("&bNow executing Bungee commands as ${target.name}")
        }
        listen<ChatEvent>(LOW) {
            val player = it.sender as? ProxiedPlayer
                ?: return@listen

            val data = Data.Player(player).target.not("")
                ?: return@listen

            if(it.message.toLowerCase() == "/gsu"){
                it.isCancelled = true
                return@listen player.exit()
            }

            val target = proxy.getPlayer(data)
                ?: return@listen player.msg("&c$data is not online")

            proxy.pluginManager.dispatchCommand(
                RedirectedPlayer(
                    player,
                    target
                ), it.message.drop(1)).also{
                success -> if(success) it.isCancelled = true
            }
        }
    }
}

object Data: PluginConfigFile("data"){
    class Player(player: ProxiedPlayer){
        var target by string(player.uniqueId.toString())
    }
}

fun ProxiedPlayer.exit(){
    Data.Player(this).target = ""
    msg("&bExited Bungee su")
}

fun ProxiedPlayer.asOp() = OppedPlayer(this)
fun CommandSender.asOp() = OppedSender(this)

class RedirectedPlayer(val sub: ProxiedPlayer, val target: ProxiedPlayer): ProxiedPlayer by target{
    override fun sendMessage(message: String?) = sub.sendMessage("[${target.name}] $message")
    override fun sendMessage(message: BaseComponent?) {
        textOf("[${target.name}] ").apply {
            addExtra(message)
            sub.sendMessage(this)
        }
    }
}

class OppedPlayer(val sub: ProxiedPlayer): ProxiedPlayer by sub{
    override fun hasPermission(name: String?) = true
}

class OppedSender(val sub: CommandSender): CommandSender by sub{
    override fun hasPermission(name: String?) = true
}