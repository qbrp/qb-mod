package org.qbrp.client.core.networking.utils

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.qbrp.main.core.game.storage.Storage

fun Storage<*>.enableClear() {
    ClientPlayConnectionEvents.DISCONNECT.register { _, _, ->
        clear()
    }
}