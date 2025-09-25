package org.qbrp.main.engine.items.components

interface ItemTick: StateTick, ExactTick {
    override fun tickStateExact() {}
}
