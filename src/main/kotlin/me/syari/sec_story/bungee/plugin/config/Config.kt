package me.syari.sec_story.bungee.plugin.config

import me.syari.sec_story.bungee.ServerEvent.allowCommand
import me.syari.sec_story.bungee.discord.Discord
import me.syari.sec_story.bungee.discord.DiscordChannel
import me.syari.sec_story.bungee.lib.LoadedServer
import me.syari.sec_story.bungee.login.LoginEvent.playerDataPath
import me.syari.sec_story.bungee.lib.Message.send
import me.syari.sec_story.bungee.plugin.Plugin.mainServer
import me.syari.sec_story.bungee.plugin.Plugin.plugin
import me.syari.sec_story.bungee.plugin.command.CreateCommand.createCmd
import me.syari.sec_story.bungee.plugin.init.FunctionInit
import me.syari.sec_story.bungee.whitelist.WhiteList.loadWhiteList
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File

object Config : FunctionInit {
    override fun init() {
        createCmd("gsr"){ sender, _ ->
            sender.send("&b[GSR] &fReload...")
            loadConfig()
            sender.send("&b[GSR] &fReloaded...")
        }
    }

    private val provider = ConfigurationProvider.getProvider(YamlConfiguration::class.java)

    fun Pair<Configuration, File>.edit(run: Configuration.() -> Unit){
        val config = first
        run.invoke(config)
        config.save(second)
    }

    private fun Configuration.save(file: File){
        provider.save(this, file)
    }

    fun config(file_name: String, run: Configuration.() -> Unit): Pair<Configuration, File> {
        if(!plugin.dataFolder.exists()){
            plugin.dataFolder.mkdir()
        }
        val file = File(plugin.dataFolder, file_name)
        if(!file.exists()){
            file.createNewFile()
        }
        val config = provider.load(file)
        run.invoke(config)
        return config to file
    }

    fun loadConfig() {
        config("config.yml") {
            playerDataPath = getString("player-data-path")
            with(Discord) {
                token = getString("discord.token")
                guild = getLong("discord.guild")
                DiscordChannel.values().forEach {
                    it.id = getLong("discord.channel.${it.raw}")
                }
            }
            allowCommand = getStringList("command.allow")
            mainServer = getString("server.main")
            LoadedServer.values().forEach {
                it.raw = getString("server.list.${it.cfg}")
            }
        }

        loadWhiteList()
    }
}