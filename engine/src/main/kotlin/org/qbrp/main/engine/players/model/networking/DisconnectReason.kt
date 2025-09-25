package org.qbrp.main.engine.players.model.networking

import net.minecraft.text.Text
import org.qbrp.main.core.utils.format.Format.formatMm

@JvmInline
value class DisconnectReason(val text: Text) {
    constructor(text: String): this(
        "<red>[ENGINE] ${text}</red>".formatMm()
    )
}