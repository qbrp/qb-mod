package org.qbrp.main.core.storage

import org.qbrp.main.core.components.serialization.Identifiable

interface ArchiveAccess {
    fun archive(obj: Identifiable)
}