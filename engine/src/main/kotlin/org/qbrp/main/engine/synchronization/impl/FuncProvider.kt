package org.qbrp.main.engine.synchronization.impl

import org.qbrp.main.engine.synchronization.`interface`.SyncObjectProvider
import org.qbrp.main.engine.synchronization.`interface`.Synchronizable
import org.qbrp.main.engine.synchronization.`interface`.Synchronizer

class FuncProvider(val sender: Synchronizer, val getter: () -> Collection<Synchronizable>): SyncObjectProvider {
    override fun provide(): Collection<Synchronizable> {
        return getter()
    }

    override fun sender(): Synchronizer {
        return sender
    }
}