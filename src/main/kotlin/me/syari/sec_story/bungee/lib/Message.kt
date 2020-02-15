package me.syari.sec_story.bungee.lib

import me.syari.sec_story.bungee.plugin.Plugin.plugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent

object Message {
    private val String.toColor get() = ChatColor.translateAlternateColorCodes('&', this)

    val String.coloredComponent get() = TextComponent(toColor)

    fun CommandSender.send(msg: String){
        sendMessage(msg.coloredComponent)
    }

    fun broadcast(msg: String){
        plugin.proxy.broadcast(msg.coloredComponent)
    }

    fun broadcast(msg: StringBuilder){
        broadcast(msg.toString())
    }

    fun broadcast(server: LoadedServer, msg: String){
        val name = server.raw
        plugin.proxy.players.forEach {
            if(it.server.info.name == name){
                it.send(msg)
            }
        }
    }
}