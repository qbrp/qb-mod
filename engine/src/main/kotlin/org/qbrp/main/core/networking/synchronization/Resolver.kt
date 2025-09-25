package org.qbrp.main.core.networking.synchronization

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.networking.PacketListener

interface Resolver<O: Identifiable>: PacketListener {
    fun resolve(packet: SynchronizePacket<O>): O?
}