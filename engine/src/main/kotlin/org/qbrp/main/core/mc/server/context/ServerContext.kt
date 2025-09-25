package org.qbrp.main.core.mc.server.context

import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.core.networking.require.RequireManager

interface ServerContext {
    val packetSender: ServerPacketSender
    val postRequirePacketListener: RequireManager
}