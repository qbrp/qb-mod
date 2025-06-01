package org.qbrp.main.core.assets.common

open class AssetKey(val value: String): Key {
    override fun getId() = "qbrp/assets/${value}"
}