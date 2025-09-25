package org.qbrp.main.engine.items.components.meta

import kotlinx.serialization.Serializable
import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.components.DataComponent

/** Мета-информация объекта. Инициализируется на фабрике. */
@Serializable
data class Meta(val tag: Prefab.Tag): DataComponent {
    val key: String = "${tag.parentPrefab!!.identifier}#${tag.id}"
}