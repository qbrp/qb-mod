package org.qbrp.main.engine.world

data class RaycastFilter(val targets: List<RaycastTarget>) {
    companion object {
        val ALL = RaycastFilter(listOf(RaycastTarget.PLAYER, RaycastTarget.PROP))
    }
}