package me.syari.sec_story.bungee

import me.syari.sec_story.bungee.plugin.init.EventInit
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.event.EventHandler

object ServerEvent: EventInit {
    @EventHandler
    fun on(e: ProxyPingEvent){
        e.response.version.name = "1.12-1.12.2"
    }

    lateinit var allowCommand: List<String>

    @EventHandler
    fun onCommand(e: ChatEvent){
        if(!e.isProxyCommand) return
        val cmd = e.message.substring(1).split("\\s+".toRegex())[0]
        if(cmd in allowCommand) return
        e.isCancelled = true
    }
}