package me.syari.sec_story.bungee.whitelist

import me.syari.sec_story.bungee.plugin.config.Config.config
import me.syari.sec_story.bungee.plugin.config.Config.edit
import me.syari.sec_story.bungee.lib.Message.coloredComponent
import me.syari.sec_story.bungee.plugin.command.CreateCommand.createCmd
import me.syari.sec_story.bungee.lib.Message.send
import me.syari.sec_story.bungee.plugin.init.FunctionInit
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.config.Configuration
import java.io.File

object WhiteList : FunctionInit {
    override fun init() {
        createCmd("whitelist"){ sender, args ->
            if(sender.hasPermission("whitelist") || containsWhiteList(
                    sender.name
                )
            ){
                when(args.whenIndex(0)){
                    "on" -> {
                        enable = true
                        sender.send("&b[Whitelist] &fホワイトリストを&a有効化&fしました")
                    }
                    "off" -> {
                        enable = false
                        sender.send("&b[Whitelist] &fホワイトリストを&c無効化&fしました")
                    }
                    "add" -> {
                        val raw = args.whenIndex(1) ?: return@createCmd sender.send("&b[Whitelist] &cIDを入力してください")
                        if(containsWhiteList(raw)){
                            sender.send("&b[Whitelist] &c既にホワイトリストに存在します")
                        } else {
                            editWhiteList {
                                add(raw)
                            }
                            sender.send("&b[Whitelist] &fホワイトリストに&a$raw&fを追加しました")
                        }
                    }
                    "remove" -> {
                        val raw = args.whenIndex(1) ?: return@createCmd sender.send("&b[Whitelist] &cIDを入力してください")
                        if(containsWhiteList(raw)){
                            editWhiteList {
                                remove(raw)
                            }
                            sender.send("&b[Whitelist] &fホワイトリストから&a$raw&fを削除しました")
                        } else {
                            sender.send("&b[Whitelist] &cホワイトリストに存在しません")
                        }
                    }
                    "list" -> {
                        sender.send("&b[Whitelist] &fホワイトリスト")
                        whiteList.forEach {
                            sender.send("&7- &a$it")
                        }
                    }
                    "reload" -> {
                        sender.send("&b[Whitelist] &f更新しました")
                        loadWhiteList()
                    }
                    else -> {
                        sender.send("""
                            &b[Whitelist] &fコマンド一覧
                            &7- &a/whitelist on &7ホワイトリストを有効化する
                            &7- &a/whitelist off &7ホワイトリストを無効化する
                            &7- &a/whitelist list &7ホワイトリストを表示する
                            &7- &a/whitelist add <Name> &7ホワイトリストに名前を追加する
                            &7- &a/whitelist remove <Name> &7ホワイトリストから名前を削除する
                            &7- &a/whitelist reload &7ホワイトリストを更新する
                        """.trimIndent())
                    }
                }
            } else {
                sender.send("&b[Whitelist] &c権限がありません")
            }
        }
    }

    private var loadMode = true
    private var enable = false
        set(value) {
            field = value
            if(loadMode) return
            config.edit {
                set("enable", value)
            }
        }
    private var message = ""
        set(value) {
            field = value
            if(loadMode) return
            config.edit {
                set("message", value)
            }
        }
    private var whiteList = listOf<String>()
        set(value) {
            field = value
            if(loadMode) return
            config.edit {
                set("list", value)
            }
        }

    private lateinit var config: Pair<Configuration, File>

    fun loadWhiteList(){
        loadMode = true
        try {
            config = config("whitelist.yml"){
                enable = getBoolean("enable")
                message = getString("message")
                whiteList = getStringList("list")
            }
        } finally {
            loadMode = false
        }
    }

    fun checkPlayer(e: PreLoginEvent) {
        if(enable && !containsWhiteList(
                e.connection.name
            )
        ){
            e.isCancelled = true
            e.setCancelReason(message.coloredComponent)
        }
    }

    private fun containsWhiteList(name: String) = name.toLowerCase() in whiteList

    private fun editWhiteList(run: MutableList<String>.() -> Unit){
        val list = whiteList.toMutableList()
        run.invoke(list)
        whiteList = list
    }
}