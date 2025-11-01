package org.qbrp.main.engine.containers.entity

import org.qbrp.main.core.components.serialization.Identifiable

interface EntryReference: Identifiable

fun String.asEntryReference() = object: EntryReference {
    override val id: String get() = this@asEntryReference
}