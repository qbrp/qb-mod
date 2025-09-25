package org.qbrp.main.engine.text

interface Transformer<T, C> {
    fun transform(ctx: C): T
}