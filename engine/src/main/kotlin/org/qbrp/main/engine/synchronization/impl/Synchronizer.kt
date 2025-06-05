package org.qbrp.main.engine.synchronization.impl

import org.qbrp.main.core.mc.player.PlayerObject
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.engine.synchronization.Synchronizable
import org.qbrp.main.engine.synchronization.state.SyncObjectProvider
import kotlin.concurrent.fixedRateTimer

class Synchronizer {
    private val providers: MutableList<SyncObjectProvider> = mutableListOf()
    private val playersList: List<PlayerObject> get() = PlayersUtil.playersList.toList()
    companion object {
        const val SYNC_RATE: Long = 2000L //milis
    }

    fun addProvider(provider: SyncObjectProvider) = providers.add(provider)
    fun syncForPlayers() {
        val objects = providers.map { Pair(it.provide(), it.sender()) }
        for (player in playersList) {
            for (obj in objects) {
                val provided = obj.first
                val sender = obj.second
                provided.forEach { Synchronizable.trySync(player, sender) }
                println("[${System.currentTimeMillis()}] Debug: Send to ${player.name}")
            }
        }
    }

    fun startTimer() {
        fixedRateTimer("SynchronizationTimer", true, 0, SYNC_RATE) {
            syncForPlayers()
        }
    }
}