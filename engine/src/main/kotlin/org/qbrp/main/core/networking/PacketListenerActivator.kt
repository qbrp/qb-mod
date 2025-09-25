package org.qbrp.main.core.networking

import org.qbrp.main.core.networking.sender.PacketConstructor

interface PacketListenerActivator<Ctx: NetworkContext> {
    fun run(channelName: String, listener: PacketListener, constructor: PacketConstructor<Ctx>)
}