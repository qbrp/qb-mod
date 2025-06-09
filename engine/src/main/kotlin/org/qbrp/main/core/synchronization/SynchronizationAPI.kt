package org.qbrp.main.core.synchronization

import org.qbrp.main.core.synchronization.state.SyncObjectProvider

interface SynchronizationAPI {
    fun addProvider(provider: SyncObjectProvider)
}