package me.mrkirby153.bridgebot.spigot

import me.mrkirby153.bridgebot.spigot.chat.ChatHandler
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CommandDiscordBridge(val plugin: Bridge): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.isOp){
            sender.sendMessage("${ChatColor.RED}You do not have permission to perform this command!")
            return true
        }

        if(args.size == 1){
            if(args[0].equals("reload", true)){
                plugin.reloadConfig()
                ChatHandler.reload()
                sender.sendMessage("${ChatColor.GREEN}Configuration reloaded!")
                return true
            }
        }
        sender.sendMessage("${ChatColor.RED}Unknown sub-command!")
        return true
    }
}