package org.qbrp.main.engine.players.auth

import org.qbrp.main.core.networking.PacketListener

interface RegistrationListener: PacketListener {
    fun onAuthSuccess(data: SessionData)
}