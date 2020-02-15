package me.syari.sec_story.bungee.discord

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member

object DiscordRoom {
    private fun Guild.getCategory() = getCategoriesByName("private-room", false).firstOrNull() ?: createCategory("private-room").complete()

    private fun Member.getRoom() = guild.textChannels.firstOrNull { f -> f.topic?.contains(asMention) == true }

    enum class CreateStatus {
        Success,
        HasRoom
    }

    fun Member.createRoom(name: String?): CreateStatus {
        val guild = guild
        if(getRoom() != null) return CreateStatus.HasRoom
        guild.getCategory().createTextChannel(name ?: "${effectiveName}-room")
            .setTopic("Created By $asMention.")
            .addPermissionOverride(guild.publicRole, listOf(),listOf(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_HISTORY
            ))
            .addPermissionOverride(this, listOf(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_HISTORY
            ), listOf())
            .queue()
        return CreateStatus.Success
    }

    enum class InviteStatus {
        Success,
        NobodyInvite,
        NotHasRoom
    }

    fun Member.inviteMember(members: List<Member>): InviteStatus {
        val c = getRoom() ?: return InviteStatus.NotHasRoom
        if(members.isEmpty()) return InviteStatus.NobodyInvite
        val manager = c.manager
        members.forEach { member ->
            if(c.canTalk(member)) return@forEach
            manager.putPermissionOverride(member, listOf(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_HISTORY
            ), listOf())
        }
        manager.queue()
        return InviteStatus.Success
    }

    enum class KickStatus {
        Success,
        NobodyKick,
        NotHasRoom
    }

    fun Member.kickMember(members: List<Member>): KickStatus {
        val c = getRoom() ?: return KickStatus.NotHasRoom
        if(members.isEmpty()) return KickStatus.NobodyKick
        val manager = c.manager
        members.forEach { member ->
            if(!c.canTalk(member)) return@forEach
            manager.removePermissionOverride(member)
        }
        manager.queue()
        return KickStatus.Success
    }

    enum class RenameStatus{
        Success,
        NotHasRoom,
        NoInputName
    }

    fun Member.renameRoom(name: String?): RenameStatus {
        val c = getRoom() ?: return RenameStatus.NotHasRoom
        if(name == null) return RenameStatus.NoInputName
        c.manager.setName(name).queue()
        return RenameStatus.Success
    }

    enum class DeleteStatus{
        Success,
        NotHasRoom
    }

    fun Member.deleteRoom(): DeleteStatus {
        val c = getRoom() ?: return DeleteStatus.NotHasRoom
        c.delete().queue()
        return DeleteStatus.Success
    }
}