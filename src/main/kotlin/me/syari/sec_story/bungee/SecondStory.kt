package me.syari.sec_story.bungee

import me.syari.sec_story.bungee.discord.Discord
import me.syari.sec_story.bungee.plugin.Plugin.plugin
import me.syari.sec_story.bungee.plugin.Plugin.scheduler
import me.syari.sec_story.bungee.plugin.config.Config.loadConfig
import me.syari.sec_story.bungee.plugin.init.InitRegister
import me.syari.sec_story.bungee.plugin.message.CustomChannel
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.scheduler.BungeeScheduler

class SecondStory: Plugin() {
    override fun onEnable() {
        plugin = this
        scheduler = BungeeScheduler()
        CustomChannel.values().forEach {
            proxy.registerChannel(it.id)
        }
        InitRegister.register()
        loadConfig()
        Discord.connect()
    }

    override fun onDisable() {
        Discord.disconnect()
    }
}