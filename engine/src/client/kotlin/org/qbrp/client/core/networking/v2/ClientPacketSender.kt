package org.qbrp.client.core.networking.v2

import org.qbrp.main.core.networking.Packet

interface ClientPacketSender {
    fun send(p: Packet<*>, channelName: String)
}