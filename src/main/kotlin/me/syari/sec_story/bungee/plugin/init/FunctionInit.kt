package me.syari.sec_story.bungee.plugin.init

interface FunctionInit {
    companion object {
        fun register(vararg functionInit: FunctionInit){
            functionInit.forEach {
                it.init()
            }
        }
    }

    fun init()
}