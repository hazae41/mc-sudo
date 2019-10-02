package hazae41.minecraft.sudo.bungee

import hazae41.minecraft.kutils.bungee.execute
import hazae41.minecraft.kutils.bungee.listen
import hazae41.minecraft.kutils.bungee.msg
import hazae41.minecraft.kutils.purified
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.event.EventPriority.LOW

fun Plugin.makeListeners() = listen<ChatEvent>(LOW) {
    val player = it.sender
        as? ProxiedPlayer ?: return@listen

    val targetName = Data.Player(player).target
    if (targetName.isBlank()) return@listen

    if (it.message.purified == "/gsu") {
        it.isCancelled = true
        player.exit()
        player.msg("&bExited Bungee su")
        return@listen
    }

    val target = proxy.getPlayer(targetName)
        ?: return@listen player.msg("&c$targetName is not online")

    player.asPlayer(target).execute(it.message.drop(1)).also { success ->
        if (success) it.isCancelled = true
    }
}