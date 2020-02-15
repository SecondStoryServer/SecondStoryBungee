package me.syari.sec_story.bungee

import me.syari.sec_story.bungee.plugin.Plugin.mainServerInfo
import me.syari.sec_story.bungee.plugin.command.CreateCommand.createCmd
import me.syari.sec_story.bungee.plugin.init.FunctionInit
import net.md_5.bungee.api.connection.ProxiedPlayer

object ServerHub: FunctionInit {
    override fun init() {
        createCmd("hub"){ sender, _ ->
            if(sender is ProxiedPlayer){
                sender.connect(mainServerInfo)
            }
        }
    }
}