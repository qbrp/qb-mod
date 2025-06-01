package org.qbrp.main.core.utils.networking.messages

import net.minecraft.util.Identifier
import org.qbrp.main.core.Core
import org.qbrp.main.core.utils.networking.messages.types.BilateralContent
import org.qbrp.main.core.utils.networking.messages.types.Content
import org.qbrp.main.core.utils.networking.messages.types.ReceiveContent

data class Message(val identifier: String, val content: Content) {
    val minecraftIdentifier: Identifier = Identifier(Core.MOD_ID, identifier)
    fun <T> getContent(): T {
        return (content as ReceiveContent).getData() as T
    }
}
