package org.qbrp.main.engine.containers.v2.entity

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.dataformats.StringId

interface EntryReference: Identifiable

fun String.asEntryReference() = object: EntryReference {
    override val id: String get() = this@asEntryReference
}