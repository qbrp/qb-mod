package org.qbrp.client.core.synchronization

import org.qbrp.client.core.networking.ClientNetworkUtil
import org.qbrp.main.core.synchronization.impl.LocalMessageSender

class ClientLocalMessageSender(name: String): LocalMessageSender(name, ClientNetworkUtil)