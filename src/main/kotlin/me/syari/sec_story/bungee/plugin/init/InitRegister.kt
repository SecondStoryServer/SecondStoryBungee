package me.syari.sec_story.bungee.plugin.init

import me.syari.sec_story.bungee.ServerEvent
import me.syari.sec_story.bungee.ServerHub
import me.syari.sec_story.bungee.discord.DiscordAuth
import me.syari.sec_story.bungee.login.LoginEvent
import me.syari.sec_story.bungee.plugin.Plugin.plugin
import me.syari.sec_story.bungee.plugin.config.Config
import me.syari.sec_story.bungee.plugin.message.PluginMessageEvent
import me.syari.sec_story.bungee.whitelist.WhiteList
import net.md_5.bungee.api.plugin.Listener

object InitRegister {
    fun register(){
        EventInit.register(plugin,
            LoginEvent,
            ServerEvent,
            PluginMessageEvent
        )
        FunctionInit.register(
            DiscordAuth,
            ServerHub,
            Config,
            WhiteList
        )
    }
}