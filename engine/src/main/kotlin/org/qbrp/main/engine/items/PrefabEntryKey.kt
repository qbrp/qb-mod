package org.qbrp.main.engine.items

import org.qbrp.main.core.assets.prefabs.PrefabKey

class PrefabEntryKey(dir: String, val tag: String): PrefabKey(dir) {
    companion object {
        fun parseString(string: String): PrefabEntryKey {
            val dir = string.split("#")[0]
            val tag = string.split("#")[1]
            return PrefabEntryKey(dir, tag)
        }
    }
}