package org.qbrp.client.core.mc.connection

import org.qbrp.main.core.events.Event

data class DisconnectEvent(val connection: ClientConnection) : Event