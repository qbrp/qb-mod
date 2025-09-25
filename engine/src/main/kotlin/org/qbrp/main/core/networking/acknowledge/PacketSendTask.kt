package org.qbrp.main.core.networking.acknowledge

import kotlinx.coroutines.Job

interface PacketSendTask {
    val id: Long
    suspend fun waitAcknowledge(retryAttempts: Int = 10, retryTime: Int = 500)
    suspend fun log(name: String): PacketSendTask
    fun queue(): PacketSendTask
}