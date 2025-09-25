package org.qbrp.client.core.networking.utils

import org.qbrp.main.core.components.serialization.DTO

interface ClientDtoSyncListener {
    fun onDtoReceived(dto: DTO)
}