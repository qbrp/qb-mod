package org.qbrp.main.core.synchronization.impl

import org.qbrp.main.core.synchronization.state.SyncObjectProvider
import org.qbrp.main.core.synchronization.Synchronizable
import org.qbrp.main.core.synchronization.Synchronizer

class FuncProvider(val sender: Synchronizer, val getter: () -> Collection<Synchronizable>): SyncObjectProvider {
    override fun provide(): Collection<Synchronizable> {
        return getter()
    }

    override fun sender(): Synchronizer {
        return sender
    }
}