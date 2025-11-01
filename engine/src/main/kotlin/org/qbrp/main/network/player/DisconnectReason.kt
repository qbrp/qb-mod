package org.qbrp.main.network.player

import net.minecraft.text.Text
import org.qbrp.main.core.util.format.Format.formatMm

@JvmInline
value class DisconnectReason(val text: Text) {
    constructor(text: String): this(
        "<red>[ENGINE] ${text}</red>".formatMm()
    )
}