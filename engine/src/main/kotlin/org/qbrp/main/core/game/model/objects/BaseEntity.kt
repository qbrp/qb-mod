package org.qbrp.main.core.game.model.objects

import net.minecraft.item.ItemStack
import org.qbrp.main.core.game.serialization.Identifiable
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry

abstract class BaseEntity<T : BaseEntity<T>>(
    open override val id: String,
): Identifiable {

    companion object {
        val IDENTIFIER_ENTRY = ClusterEntry<String>("id")
    }
}
