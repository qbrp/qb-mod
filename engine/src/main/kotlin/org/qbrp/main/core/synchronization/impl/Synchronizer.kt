package org.qbrp.main.core.synchronization.impl

import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.mc.player.PlayersUtil
import org.qbrp.main.core.synchronization.state.SyncObjectProvider

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import org.qbrp.main.core.synchronization.Synchronizable
import org.qbrp.main.core.synchronization.Synchronizer

class Synchronizer {
    private val providers: MutableList<SyncObjectProvider> = mutableListOf()

    companion object {
        const val SYNC_RATE_TICKS = 50
    }

    private var tickCounter = 0

    fun addProvider(provider: SyncObjectProvider) {
        providers.add(provider)
    }

    private fun syncForPlayers() {
        val providersSnapshot: List<SyncObjectProvider> = synchronized(providers) {
            ArrayList(providers)
        }

        val dataSnapshots: List<Pair<List<Synchronizable>, Synchronizer>> =
            providersSnapshot.map { provider ->
                val rawList: List<Synchronizable> = provider.provide().toList()
                val safeCopy: List<Synchronizable> = ArrayList(rawList)
                val sender = provider.sender()
                Pair(safeCopy, sender)
            }

        val playersList: List<ServerPlayerObject> = PlayersUtil.playersList.toList()

        for (player in playersList) {
            for ((syncables, sender) in dataSnapshots) {
                for (syncable in syncables) {
                    syncable.trySync(player, sender)
                }
            }
        }
    }

    fun start() {
        ServerTickEvents.END_WORLD_TICK.register { serverWorld ->
            tickCounter++
            if (tickCounter >= SYNC_RATE_TICKS) {
                tickCounter = 0
                syncForPlayers()
            }
        }
    }
}
