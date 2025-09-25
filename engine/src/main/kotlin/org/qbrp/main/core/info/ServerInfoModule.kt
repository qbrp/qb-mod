package org.qbrp.main.core.info

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.get
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.withEventContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.ModLoadedEvent

@Autoload(env = EnvType.SERVER)
class ServerInfoModule: EngineModule("server-info"), ServerInfoAPI {
    override val COMPOSER by lazy { ServerInformationComposer(get()) }

    override fun getKoinModule() = onlyApi<ServerInfoAPI>(this)

    override fun onEnable() = withEventContext {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            send(handler.player)
        }
        on<ModLoadedEvent> { build() }
    }

    override fun broadcast() {
        build()
        COMPOSER.send()
    }

    fun build() = COMPOSER.build()

    fun send(player: ServerPlayerEntity) = COMPOSER.send(player)
}