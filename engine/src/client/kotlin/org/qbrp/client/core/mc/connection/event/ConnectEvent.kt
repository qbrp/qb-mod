package org.qbrp.client.core.mc.connection.event

import org.qbrp.client.core.mc.connection.ClientConnection
import org.qbrp.main.core.events.Event

data class ConnectEvent(val connection: ClientConnection): Event