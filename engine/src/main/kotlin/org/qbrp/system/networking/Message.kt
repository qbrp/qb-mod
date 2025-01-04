package org.qbrp.system.networking

import net.minecraft.util.Identifier
import org.qbrp.core.Core

data class Message(val identifier: String, val content: MessageContent) {
    val minecraftIdentifier: Identifier = Identifier(Core.MOD_ID, identifier)
}
