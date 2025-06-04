package org.qbrp.main.engine.synchronization.`interface`

interface SyncObjectProvider {
    fun provide(): Collection<Synchronizable>
    fun sender(): Synchronizer
}