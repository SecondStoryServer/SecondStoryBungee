package me.syari.sec_story.bungee.plugin.init

import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin

interface EventInit: Listener {
    companion object {
        fun register(plugin: Plugin, vararg eventInit: EventInit){
            eventInit.forEach {
                plugin.proxy.pluginManager.registerListener(me.syari.sec_story.bungee.plugin.Plugin.plugin, it)
            }
        }
    }
}