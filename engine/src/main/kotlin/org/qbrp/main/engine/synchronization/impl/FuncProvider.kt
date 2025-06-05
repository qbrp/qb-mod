package org.qbrp.main.engine.synchronization.impl

import org.qbrp.main.engine.synchronization.state.SyncObjectProvider
import org.qbrp.main.engine.synchronization.Synchronizable
import org.qbrp.main.engine.synchronization.Synchronizer

class FuncProvider(val sender: Synchronizer, val getter: () -> Collection<Synchronizable>): SyncObjectProvider {
    override fun provide(): Collection<Synchronizable> {
        return getter()
    }

    override fun sender(): Synchronizer {
        return sender
    }
}