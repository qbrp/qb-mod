package org.qbrp.main.core.util

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.storage.Storage

fun <E: Identifiable> Collection<Identifiable>.getEntities(storage: Storage<E>): List<E?> {
    return map { storage.getById(it.stringId) }
}

fun <E: Identifiable> Identifiable.getEntity(storage: Storage<E>): E? {
    return storage.getById(stringId)
}

fun String.toId(): StringId {
    return StringId(this)
}

fun Collection<String>.toIds(): Collection<StringId> {
    return map { it.toId() }
}