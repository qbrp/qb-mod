package org.qbrp.main.engine.synchronization.state

import org.qbrp.main.engine.synchronization.Synchronizable
import org.qbrp.main.engine.synchronization.Synchronizer

interface SyncObjectProvider {
    fun provide(): Collection<Synchronizable>
    fun sender(): Synchronizer
}