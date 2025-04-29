package org.qbrp.core.game.model.tick

interface Tick<Context> {
    fun tick(context: Context)
}