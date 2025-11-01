package org.qbrp.client.core.networking.utils

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.qbrp.main.core.util.storage.Storage

fun Storage<*>.clearOnDisconnect() {
    ClientPlayConnectionEvents.DISCONNECT.register { _, _, ->
        clear()
    }
}