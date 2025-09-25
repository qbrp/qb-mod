package org.qbrp.client.core.networking.v2

import org.qbrp.main.core.networking.require.RequireId

interface RequirePacketHandlerManager {
    fun registerHandler(id: RequireId, handler: RequireHandler)
}