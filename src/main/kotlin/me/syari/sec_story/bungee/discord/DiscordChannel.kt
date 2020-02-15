package me.syari.sec_story.bungee.discord

enum class DiscordChannel(val raw: String, var id: Long){
    Global("global", 0L),
    ServerLog("log", 0L),
    Guid("guid", 0L),
    Join("join", 0L)
}