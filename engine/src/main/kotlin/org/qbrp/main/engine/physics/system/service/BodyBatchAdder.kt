package org.qbrp.main.engine.physics.system.service

import org.qbrp.main.engine.physics.system.entity.BodyId

interface BodyBatchAdder {
    fun prepare(ids: Set<BodyId>)
    fun finalize(ids: Set<BodyId>)
    fun abort(ids: Set<BodyId>)
}