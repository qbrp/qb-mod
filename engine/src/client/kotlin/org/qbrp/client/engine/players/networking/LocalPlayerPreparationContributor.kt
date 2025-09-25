package org.qbrp.client.engine.players.networking

import org.qbrp.client.core.networking.preparation.PreparationContributor
import org.qbrp.client.engine.players.ClientPlayerManager

class LocalPlayerPreparationContributor(private val clientPlayerManager: ClientPlayerManager): PreparationContributor {
    override fun prepared(): Boolean {
        return clientPlayerManager.isInstantiated
    }
}