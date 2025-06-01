package org.qbrp.main.core.storage

import org.qbrp.main.core.game.serialization.Identifiable

interface ArchiveAccess {
    fun archive(obj: Identifiable)
}