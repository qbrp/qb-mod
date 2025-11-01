package org.qbrp.client.core

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.client.engine.players.ClientPlayerObject

fun KoinComponent.getClientPlayer(): ClientPlayerObject? {
    return get<ClientPlayerManager>().clientPlayer
}

fun KoinComponent.isPlayerInstantiated(): Boolean {
    return get<ClientPlayerManager>().isInstantiated
}

fun onClientTick(r: () -> Unit) {
    ClientTickEvents.END_CLIENT_TICK.register { client ->
        r()
    }
}

fun onClientWorldTick(r: () -> Unit) {
    ClientTickEvents.END_WORLD_TICK.register { client ->
        r()
    }
}


fun inGame(): Boolean = MinecraftClient.getInstance().world != null