package org.qbrp.engine

import net.minecraft.server.MinecraftServer
import org.qbrp.core.resources.ServerResources
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.music.MusicManagerModule
import org.qbrp.engine.spectators.SpectatorsModule

class Engine {

    companion object {
        lateinit var musicManagerModule: MusicManagerModule
        lateinit var spectatorsModule: SpectatorsModule
        lateinit var chatModule: ChatModule
    }

    // Подразумевает, что Core был полностью загружен
    fun initialize(server: MinecraftServer) {
        musicManagerModule = MusicManagerModule(server, ServerResources.getConfig().music, ServerResources.getConfig().databases)
        musicManagerModule.load()
        spectatorsModule = SpectatorsModule()
        chatModule = ChatModule(ServerResources.getConfig().chat, server)
    }
}
