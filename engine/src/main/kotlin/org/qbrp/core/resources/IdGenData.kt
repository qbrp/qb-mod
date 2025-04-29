package org.qbrp.core.resources

import org.qbrp.core.resources.data.Data

class IdGenData(var id: Long = 0): Data() {
    override fun toFile(): String {
        return gson.toJson(this)
    }
}