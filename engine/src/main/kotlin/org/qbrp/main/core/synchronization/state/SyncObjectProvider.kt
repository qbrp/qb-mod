package org.qbrp.main.core.synchronization.state

import org.qbrp.main.core.synchronization.Synchronizable
import org.qbrp.main.core.synchronization.Synchronizer

interface SyncObjectProvider {
    fun provide(): Collection<Synchronizable>
    fun sender(): Synchronizer
}