package org.qbrp.core.game

import org.qbrp.core.resources.IdGenData
import org.qbrp.core.resources.ServerResources

object IDGenerator {
    private val idGen get() = ServerResources.getRootBranch().idGen
    private val idGenData get() = idGen.data as IdGenData

    fun nextId(): Long {
        return idGenData.id++.also {
            idGen.save()
        }
    }

}