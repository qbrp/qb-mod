package org.qbrp.main.core.mc.player.dataformats

@JvmInline
value class Username(val value: String) {
    init {
        require(value.none { it.isWhitespace() })
    }
    override fun toString(): String = value
}