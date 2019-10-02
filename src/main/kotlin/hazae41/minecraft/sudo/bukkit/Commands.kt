package hazae41.minecraft.sudo.bukkit

import hazae41.minecraft.kutils.bukkit.command
import hazae41.minecraft.kutils.bukkit.execute
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.catch
import hazae41.minecraft.kutils.error
import org.bukkit.entity.Player

fun Plugin.makeCommands() {
    command("sudo") { args ->
        if (args.isEmpty()) {
            msg("&cUsage: /sudo <command>")
            return@command
        }

        val cmd = args.joinToString(" ")
        val opped = (this as? Player)?.asOp(true) ?: asOp(true)
        opped.execute(cmd)
    }

    command("asop") { args ->
        if (args.isEmpty()) {
            msg("&cUsage: /asop <command>")
            return@command
        }

        val cmd = args.joinToString(" ")
        val opped = (this as? Player)?.asOp() ?: asOp()
        opped.execute(cmd)
    }

    command("su") { args ->
        catch<Exception>(::msg) {
            if (this !is Player)
                error("&cYou're not a player")

            val arg = args.getOrNull(0)
                ?: error("&cUsage: /su <target>")

            val target = server.matchPlayer(arg).getOrNull(0)
                ?: error("&cUnknown player")

            data.target = target.name
            msg("&bNow executing Bukkit commands as ${target.name}")
        }
    }
}