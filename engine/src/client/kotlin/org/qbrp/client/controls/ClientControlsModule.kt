package org.qbrp.client.controls

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.core.getClientPlayer
import org.qbrp.client.core.isPlayerInstantiated
import org.qbrp.client.core.onClientTick
import org.qbrp.client.core.onClientWorldTick
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.ModLoadedEvent
import org.qbrp.main.engine.physics.player.MovementInput
import org.qbrp.main.engine.physics.player.MovementInputComponent

@Autoload(env = EnvType.CLIENT)
class ClientControlsModule : EngineModule("client-controls") {
    override fun getKoinModule(): Module = innerWithApi({
        single { ClientInteractionManager() }
        single { ClientInteractionModeManager() }
    }, {
    })

    override fun onEnable() {
        val client = MinecraftClient.getInstance()
        onClientWorldTick {
            if (!isPlayerInstantiated()) return@onClientWorldTick
            val playerInput = getClientPlayer()?.get<MovementInputComponent>() ?: return@onClientWorldTick
            updateMovementInput(playerInput, client)
        }

        on<ModLoadedEvent>() {
            registerKeybinds(getKoin())
        }
    }
}