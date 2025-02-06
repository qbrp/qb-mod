package org.qbrp.engine.client.engine.spectators

import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.networking.messages.Messages.SPAWN

class SpectatorsModuleClient {
    fun spawnPlayer() {
        ClientNetworkManager.sendSignal(SPAWN)
    }

}