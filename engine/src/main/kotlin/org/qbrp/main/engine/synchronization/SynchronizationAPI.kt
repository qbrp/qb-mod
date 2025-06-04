package org.qbrp.main.engine.synchronization

import org.qbrp.main.engine.synchronization.`interface`.SyncObjectProvider

interface SynchronizationAPI {
    fun addProvider(provider: SyncObjectProvider)
}