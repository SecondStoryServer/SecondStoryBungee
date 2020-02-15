package me.syari.sec_story.bungee.login

import me.syari.sec_story.bungee.discord.Discord
import me.syari.sec_story.bungee.discord.DiscordChannel
import me.syari.sec_story.bungee.lib.Message.broadcast
import me.syari.sec_story.bungee.plugin.Plugin.plugin
import me.syari.sec_story.bungee.plugin.Plugin.scheduler
import me.syari.sec_story.bungee.plugin.init.EventInit
import me.syari.sec_story.bungee.whitelist.WhiteList.checkPlayer
import net.md_5.bungee.api.event.*
import net.md_5.bungee.event.EventHandler
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

object LoginEvent : EventInit {
    lateinit var playerDataPath: String

    private val postLogin = mutableSetOf<UUID>()

    @EventHandler
    fun on(e: PreLoginEvent){
        val uuid = e.connection.uniqueId
        checkPlayer(e)
    }

    @EventHandler
    fun on(e: PostLoginEvent){
        val p = e.player
        val uuid = p.uniqueId
        postLogin.add(uuid)
        scheduler.schedule(plugin, {
            postLogin.remove(uuid)
        }, 20L, TimeUnit.SECONDS)
    }

    @EventHandler
    fun on(e: ServerConnectedEvent){
        val p = e.player
        val uuid = p.uniqueId
        if(postLogin.contains(uuid)){
            postLogin.remove(uuid)
            val playerData = File(playerDataPath, "$uuid.dat")
            val msg = StringBuilder("&7&l >> ")
            if(playerData.exists()){
                Discord.send(DiscordChannel.Global, "**>> Join ${p.displayName}**")
                msg.append("&a&lJoin ")
            } else {
                Discord.send(DiscordChannel.Global, "**>> FirstJoin ${p.displayName}**")
                msg.append("&e&lFirstJoin ")
            }
            msg.append("&f&l" + p.displayName)
            broadcast(msg)
        } else {
            broadcast("&7&l >> &3&lSwitch &f&l" + p.displayName)
        }
    }

    @EventHandler
    fun on(e: PlayerDisconnectEvent){
        val p = e.player
        val uuid = p.uniqueId
        if(postLogin.contains(uuid)){
            postLogin.remove(uuid)
            return
        }
        Discord.send(DiscordChannel.Global, "**>> Quit ${p.displayName}**")
        broadcast("&7&l >> &c&lQuit &f&l" + p.displayName)
    }
}