package hazae41.minecraft.sudo.bungee

import hazae41.minecraft.kutils.textOf
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

open class OppedPlayer(val sub: ProxiedPlayer) : ProxiedPlayer by sub {
    override fun hasPermission(name: String?) = true
}

open class OppedSender(val sub: CommandSender) : CommandSender by sub {
    override fun hasPermission(name: String?) = true
}

open class RedirectedPlayer(val sub: ProxiedPlayer, val target: ProxiedPlayer) : ProxiedPlayer by target {

    override fun sendMessage(message: String?) {
        sub.sendMessage("[${target.name}] $message")
    }

    override fun sendMessage(message: BaseComponent?) {
        val text = textOf("[${target.name}] ")
        text.addExtra(message)
        sub.sendMessage(text)
    }
}