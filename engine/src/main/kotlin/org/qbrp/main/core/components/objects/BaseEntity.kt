package org.qbrp.main.core.components.objects

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.networking.messages.components.ClusterEntry

@Serializable
abstract class BaseEntity<T : BaseEntity<T>>(
    open override val id: String,
): Identifiable {

    companion object {
        val IDENTIFIER_ENTRY = ClusterEntry<String>("id")
    }
}
