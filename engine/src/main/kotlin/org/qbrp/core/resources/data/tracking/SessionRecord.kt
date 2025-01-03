package org.qbrp.core.resources.data.tracking

import org.qbrp.core.resources.data.Data

class SessionRecord(val record: MutableList<Record> = mutableListOf()): Data() {
    override fun toFile(): String {
        TODO("Not yet implemented")
    }
}