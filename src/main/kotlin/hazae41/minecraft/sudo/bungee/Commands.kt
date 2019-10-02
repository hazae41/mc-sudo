package hazae41.minecraft.sudo.bungee

import hazae41.minecraft.kutils.bungee.command
import hazae41.minecraft.kutils.bungee.execute
import hazae41.minecraft.kutils.bungee.msg
import hazae41.minecraft.kutils.catch
import hazae41.minecraft.kutils.error
import net.md_5.bungee.api.connection.ProxiedPlayer

fun Plugin.makeCommands() {
    command("gsudo", "sudo.sudo") { args ->
        catch<Exception>(::msg) {
            if (args.isEmpty())
                error("&cUsage: /gsudo <command>")

            val cmd = args.joinToString(" ")
            val asop = (this as? ProxiedPlayer)?.asOp() ?: asOp()

            asop.execute(cmd).also { success ->
                if (!success) msg("&cUnknown command")
            }
        }
    }

    command("gsu", "sudo.su") { args ->
        catch<Exception>(::msg) {
            if (this !is ProxiedPlayer)
                error("&cYou're not a player")

            val arg = args.getOrNull(0)
                ?: error("&cUsage: /gsu <target>")

            val target = proxy.matchPlayer(arg).elementAtOrNull(0)
                ?: error("&cUnknown player")

            data.target = target.name
            msg("&bNow executing Bungee commands as ${target.name}")
        }
    }
}