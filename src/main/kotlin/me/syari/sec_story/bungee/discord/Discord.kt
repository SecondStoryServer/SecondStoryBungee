package me.syari.sec_story.bungee.discord

import me.syari.sec_story.bungee.lib.Message.send
import me.syari.sec_story.bungee.discord.DiscordAuth.seedCode
import me.syari.sec_story.bungee.discord.DiscordAuth.codesFromPlayer
import me.syari.sec_story.bungee.discord.DiscordAuth.codesFromMember
import me.syari.sec_story.bungee.lib.LoadedServer
import me.syari.sec_story.bungee.lib.Message
import me.syari.sec_story.bungee.lib.Message.broadcast
import me.syari.sec_story.bungee.plugin.Plugin.plugin
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import javax.security.auth.login.LoginException

object Discord : ListenerAdapter() {
    var client: JDA? = null
    var guild = 0L
    lateinit var token: String

    fun connect(){
        try {
            client = JDABuilder(token)
                .addEventListeners(this)
                .build()
        } catch (ex: LoginException){
            error("Discordのログインに失敗しました")
        }
    }

    override fun onGuildMemberJoin(e: GuildMemberJoinEvent) {
        val member = e.member
        val code = member.seedCode()
        member.dm("""
            :v: SecondStory Discord ご参加ありがとうございます
            アカウント認証をしなければほとんどの機能を使うことが出来なくなっています。ご協力をお願いします。
            :point_up: 認証コードは「**$code**」です。マイクラサーバー内で「**/auth $code**」と入力してください。
        """.trimIndent())
    }

    override fun onPrivateMessageReceived(e: PrivateMessageReceivedEvent) {
        val g = client?.getGuildById(guild) ?: return
        val a = e.author
        if(a.isBot) return
        val id = a.idLong
        val member = g.getMemberById(id) ?: return
        if(member.roles.firstOrNull { f -> f.name == "Player" } != null){
            e.channel.sendMessage(":v: 既に認証されています")
            return
        }
        codesFromMember.values.removeIf{ r -> r == member.idLong }
        val code = member.seedCode()
        e.channel.sendMessage(":point_up: 認証コードは「**$code**」です。サーバー内で「**/auth $code**」と入力してください。").queue()
    }

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        DiscordCommand.onMessage(e)
        val author = e.message.author
        if(author.isBot) return
        val msg = e.message.contentDisplay
        val guild = e.guild
        val name = guild.getMember(author)?.nickname ?: author.name
        val channel = e.message.channel
        when(DiscordChannel.values().firstOrNull { c -> c.id == channel.idLong }){
            DiscordChannel.Global -> broadcast(LoadedServer.Lobby, "&6&lGlobal &5Discord &f$name &b≫ &f$msg")
            DiscordChannel.Guid -> {
                val member = guild.getMember(author) ?: return
                val role = guild.roles.firstOrNull { r -> r.name == "Player" } ?: return
                if(member.roles.contains(role)) return
                e.message.delete().queue()
                val uuid = codesFromPlayer[msg] ?: return
                val p = plugin.proxy.getPlayer(uuid) ?: return
                guild.addRoleToMember(member, role).queue()
                codesFromPlayer.values.removeIf{ r -> r == uuid }
                codesFromMember.values.removeIf{ r -> r == member.idLong }
                if(p.isConnected) p.send("&b[Discord] &a${name}&fとして認証しました")
                send(
                    DiscordChannel.Join,
                    ":v: **Join ${member.asMention} - ${p.name}**"
                )
            }
            else -> {}
        }
    }

    fun send(ch: DiscordChannel, msg: String){
        val txt = client?.getTextChannelById(ch.id) ?: return
        txt.sendMessage(msg.replace("_", "\\_")).queue()
    }

    private fun User.dm(msg: String){
        openPrivateChannel().queue { channel ->
            channel.sendMessage(msg).queue()
        }
    }

    private fun Member.dm(msg: String){ user.dm(msg) }

    fun disconnect(){
        try {
            client?.shutdownNow()
        } catch (ex: Exception){}
    }
}