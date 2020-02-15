package me.syari.sec_story.bungee.plugin

import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.scheduler.BungeeScheduler

object Plugin {
    lateinit var plugin: Plugin

    lateinit var scheduler: BungeeScheduler

    lateinit var mainServer: String

    val mainServerInfo: ServerInfo by lazy { plugin.proxy.getServerInfo(mainServer) }
}