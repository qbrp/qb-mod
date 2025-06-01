package org.qbrp.main.core.info

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.engine.ModInitializedEvent

@Autoload(env = EnvType.SERVER)
class ServerInfoModule: QbModule("server-info"), ServerInfoAPI {
    override val COMPOSER = ServerInformationComposer()

    override fun getKoinModule() = onlyApi<ServerInfoAPI>(this)

    override fun onEnable() {
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            send(handler.player)
        }
        ModInitializedEvent.EVENT.register(::build)
    }

    override fun broadcast() {
        build()
        COMPOSER.send()
    }

    fun build() = COMPOSER.build()

    fun send(player: ServerPlayerEntity) = COMPOSER.send(player)
}