package hazae41.minecraft.sudo.bukkit

import hazae41.minecraft.kutils.bukkit.execute
import hazae41.minecraft.kutils.bukkit.listen
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.purified
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.player.PlayerCommandPreprocessEvent

fun Plugin.makeListeners() = listen<PlayerCommandPreprocessEvent>(LOW) {
    val player = it.player
    val targetName = player.data.target
    if (targetName.isBlank()) return@listen

    it.isCancelled = true

    if (it.message.purified == "/su") {
        player.exit()
        player.msg("&bExited Bukkit su")
        return@listen
    }

    val target = server.getPlayer(targetName)
        ?: return@listen player.msg("&c$targetName is not online")

    player.asPlayer(target).execute(it.message.drop(1))
}