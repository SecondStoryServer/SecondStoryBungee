package me.syari.sec_story.bungee.discord

import me.syari.sec_story.bungee.discord.DiscordRoom.createRoom
import me.syari.sec_story.bungee.discord.DiscordRoom.deleteRoom
import me.syari.sec_story.bungee.discord.DiscordRoom.inviteMember
import me.syari.sec_story.bungee.discord.DiscordRoom.kickMember
import me.syari.sec_story.bungee.discord.DiscordRoom.renameRoom
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

object DiscordCommand {
    fun onMessage(e: GuildMessageReceivedEvent){
        val msg = e.message.contentDisplay
        val member = e.member ?: return
        val s = msg.split("\\s+".toRegex())
        if(s[0].toLowerCase() != "/ss") return
        val c = e.channel
        when(s.getOrNull(1)?.toLowerCase()){
            "room" -> {
                if(member.roles.firstOrNull { f -> f.name in listOf("RoomCreator") } == null) return c.sendMessage("寄付者限定のコマンドです").queue()
                when(s.getOrNull(2)?.toLowerCase()){
                    "create" -> when(member.createRoom(s.getOrNull(3))){
                        DiscordRoom.CreateStatus.Success -> {
                            c.sendMessage("新しくルームを作りました").queue()
                        }
                        DiscordRoom.CreateStatus.HasRoom -> {
                            c.sendMessage("あなたは既にルームを持っています").queue()
                        }
                    }
                    "invite" -> when(member.inviteMember(e.message.mentionedMembers)){
                        DiscordRoom.InviteStatus.Success -> {
                            c.sendMessage("ルームに招待しました").queue()
                        }
                        DiscordRoom.InviteStatus.NobodyInvite -> {
                            c.sendMessage("招待するメンバーを入力していません").queue()
                        }
                        DiscordRoom.InviteStatus.NotHasRoom -> {
                            c.sendMessage("あなたはルームを持っていません").queue()
                        }
                    }
                    "kick" -> when(member.kickMember(e.message.mentionedMembers)){
                        DiscordRoom.KickStatus.Success -> {
                            c.sendMessage("ルームからキックしました").queue()
                        }
                        DiscordRoom.KickStatus.NobodyKick -> {
                            c.sendMessage("キックするメンバーを入力していません").queue()
                        }
                        DiscordRoom.KickStatus.NotHasRoom -> {
                            c.sendMessage("あなたはルームを持っていません").queue()
                        }
                    }
                    "name" -> when(member.renameRoom(s.getOrNull(3))){
                        DiscordRoom.RenameStatus.Success -> {
                            c.sendMessage("ルームの名前を変更しました").queue()
                        }
                        DiscordRoom.RenameStatus.NotHasRoom -> {
                            c.sendMessage("あなたはルームを持っていません").queue()
                        }
                        DiscordRoom.RenameStatus.NoInputName -> {
                            c.sendMessage("新しいルームの名前を入力していません").queue()
                        }
                    }
                    "delete" -> when(member.deleteRoom()){
                        DiscordRoom.DeleteStatus.Success -> {
                            c.sendMessage("ルームを削除しました").queue()
                        }
                        DiscordRoom.DeleteStatus.NotHasRoom -> {
                            c.sendMessage("あなたはルームを持っていません").queue()
                        }
                    }
                    else -> c.sendMessage("""
                        **/ss room create [Name]** ルームを作成します
                        **/ss room invite ${member.asMention}** ルームに招待します(複数人可)
                        **/ss room kick ${member.asMention}** ルームからキックします(複数人可)
                        **/ss room name <Name>** ルームの名前を変更します
                        **/ss room delete** ルームを削除します
                        """.trimIndent()).queue()

                }
            }
            else -> c.sendMessage("""
                :v: **Second Story Discord Command**
                **/ss room** ルーム関連のコマンドです
                """.trimIndent()).queue()
        }
    }
}