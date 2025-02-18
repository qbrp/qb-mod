package org.qbrp.engine

import net.minecraft.server.MinecraftServer
import org.qbrp.core.resources.ServerResources
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.chat.core.messages.ChatMessageTagsBuilder
import org.qbrp.engine.damage.DamageControllerModule
import org.qbrp.engine.music.MusicManagerModule
import org.qbrp.engine.spectators.SpectatorsModule
import org.qbrp.engine.time.TimeModule
import org.qbrp.system.networking.messages.types.StringContent

class Engine {

    companion object {
        lateinit var musicManagerModule: MusicManagerModule
        lateinit var spectatorsModule: SpectatorsModule
        lateinit var chatModule: ChatModule
        lateinit var timeModule: TimeModule
    }

    // Подразумевает, что Core был полностью загружен
    fun initialize(server: MinecraftServer) {
        musicManagerModule = MusicManagerModule(server, ServerResources.getConfig().music, ServerResources.getConfig().databases)
        musicManagerModule.load()
        spectatorsModule = SpectatorsModule()
        chatModule = ChatModule(ServerResources.getConfig().chat, server)//
        timeModule = TimeModule(server, ServerResources.getConfig().time).apply { load() }
    }
}