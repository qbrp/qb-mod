package org.qbrp.main.core.networking.acknowledge

import org.qbrp.main.core.networking.PacketListener

interface AcknowledgePacketListener: PacketListener {
    fun onAcknowledge(id: Long)
}