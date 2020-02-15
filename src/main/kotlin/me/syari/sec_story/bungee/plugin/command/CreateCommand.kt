package me.syari.sec_story.bungee.plugin.command

import me.syari.sec_story.bungee.plugin.Plugin.plugin
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

object CreateCommand {
    fun createCmd(cmd: String, execute: (CommandSender, CommandArg) -> Unit){
        plugin.proxy.pluginManager.registerCommand(plugin, object : Command(cmd){
            override fun execute(sender: CommandSender, args: Array<out String>) {
                execute.invoke(sender, CommandArg(args))
            }
        })
    }
}