package org.qbrp.main.core.assets.prefabs

import org.qbrp.main.core.assets.common.AssetKey

open class PrefabKey(val category: String, val name: String): AssetKey("prefabs/${category}/$name") {
}