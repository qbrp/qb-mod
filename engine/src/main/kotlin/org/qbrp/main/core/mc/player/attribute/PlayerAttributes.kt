package org.qbrp.main.core.mc.player.attribute

data class PlayerAttributes(val attributes: Map<McAttribute, McAttributeState>) {
    fun getAttr(type: McAttribute): McAttributeState {
        return attributes[type]!!
    }
}