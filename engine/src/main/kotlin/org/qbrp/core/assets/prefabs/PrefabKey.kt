package org.qbrp.core.assets.prefabs

import org.qbrp.core.assets.common.Key

open class PrefabKey(val category: String, val name: String): Key("prefabs/${category}/$name") {
}