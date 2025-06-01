package org.qbrp.main.core.game.loop

interface Tick<Context> {
    fun tick(context: Context)
}