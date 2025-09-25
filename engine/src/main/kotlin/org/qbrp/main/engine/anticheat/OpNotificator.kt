package org.qbrp.main.engine.anticheat

import org.qbrp.main.core.mc.context.MinecraftContext

interface OpNotificator {
    context(ctx: MinecraftContext)
    fun notifyOperators(text: String)
}