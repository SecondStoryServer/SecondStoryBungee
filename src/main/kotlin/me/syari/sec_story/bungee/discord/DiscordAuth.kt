package me.syari.sec_story.bungee.discord

import me.syari.sec_story.bungee.lib.Message.send
import me.syari.sec_story.bungee.discord.Discord.client
import me.syari.sec_story.bungee.discord.Discord.guild
import me.syari.sec_story.bungee.discord.Discord.send
import me.syari.sec_story.bungee.plugin.command.CreateCommand.createCmd
import me.syari.sec_story.bungee.plugin.init.FunctionInit
import net.dv8tion.jda.api.entities.Member
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

object DiscordAuth: FunctionInit {
    override fun init() {
        createCmd("auth"){ sender, args ->
            if(sender is ProxiedPlayer){
                val code = args.getOrNull(0)
                if(code != null){
                    val id = codesFromMember[code] ?: return@createCmd sender.send("&b[Discord] &c認証に失敗しました")
                    val g = client?.getGuildById(guild) ?: return@createCmd sender.send("&b[Discord] &c公式Discordと接続できませんでした")
                    val member = g.getMemberById(id) ?: return@createCmd sender.send("&b[Discord] &cユーザーが見つかりませんでした")
                    val role = g.roles.firstOrNull { r -> r.name == "Player" } ?: return@createCmd sender.send("&b[Discord] &c認証に問題が発生しました")
                    g.addRoleToMember(member, role).queue()
                    val name = member.nickname ?: member.user.name
                    codesFromPlayer.values.removeIf{ r -> r == sender.uniqueId  }
                    codesFromMember.values.removeIf{ r -> r == id }
                    sender.send("&b[Discord] &a${name}&fとして認証しました")
                    send(DiscordChannel.Join, ":v: **Join ${member.asMention} - ${sender.name}**")
                } else {
                    codesFromPlayer.values.removeIf{ r -> r == sender.uniqueId  }
                    sender.send("&b[Discord] &f認証コードを生成しました\n&f公式Discordの&a#ご案内&fで&a${sender.seedCode()}&fと入力してください")
                }
            }
        }
    }

    val codesFromMember = mutableMapOf<String, Long>()
    val codesFromPlayer = mutableMapOf<String, UUID>()

    fun Member.seedCode(): String{
        val code = String.format("%04d", (0..9999).random())
        codesFromMember[code] = idLong
        return code
    }

    fun ProxiedPlayer.seedCode(): String{
        val code = String.format("%04d", (0..9999).random())
        codesFromPlayer[code] = uniqueId
        return code
    }
}