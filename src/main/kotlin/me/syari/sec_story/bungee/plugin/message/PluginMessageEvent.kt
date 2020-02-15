package me.syari.sec_story.bungee.plugin.message

import com.google.common.io.ByteStreams.newDataInput
import me.syari.sec_story.bungee.discord.Discord
import me.syari.sec_story.bungee.discord.DiscordChannel
import me.syari.sec_story.bungee.plugin.init.EventInit
import net.md_5.bungee.api.event.PluginMessageEvent
import net.md_5.bungee.event.EventHandler

object PluginMessageEvent : EventInit {
    @EventHandler
    fun on(e: PluginMessageEvent){
        val contents = newDataInput(e.data)
        when(CustomChannel.values().firstOrNull { it.id == e.tag }){
            CustomChannel.Discord -> {
                when(contents.readUTF()){
                    "msg" -> {
                        val rawChannel = contents.readUTF()
                        val channel = DiscordChannel.values().firstOrNull { it.raw == rawChannel } ?: throw PluginMessageReceiveException(
                            "Discord $rawChannel is not found."
                        )
                        Discord.send(channel, contents.readUTF())
                    }
                }
            }
        }
    }
}