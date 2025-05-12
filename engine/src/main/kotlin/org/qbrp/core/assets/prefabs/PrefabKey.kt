package org.qbrp.core.assets.prefabs

import org.qbrp.core.assets.common.AssetKey

open class PrefabKey(val category: String, val name: String): AssetKey("prefabs/${category}/$name") {
}