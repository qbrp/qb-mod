package org.qbrp.main.core.game.loop

fun interface Tick<Context> {
    fun tick(context: Context)
}