package org.qbrp.main.core.networking.require

import org.qbrp.main.core.networking.PacketListener

interface RequirePacketListener: PacketListener {
    fun onRequire(id: RequireId, requestId: RequestId)
}