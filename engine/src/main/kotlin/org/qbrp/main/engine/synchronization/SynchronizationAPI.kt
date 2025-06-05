package org.qbrp.main.engine.synchronization

import org.qbrp.main.engine.synchronization.state.SyncObjectProvider

interface SynchronizationAPI {
    fun addProvider(provider: SyncObjectProvider)
}