package org.qbrp.main.core.assets.common

import org.qbrp.main.core.assets.common.Asset

class AssetsStorage {
    private val assets: MutableMap<String, Asset> = mutableMapOf()

    fun addAsset(key: String, asset: Asset) {
        assets[key] = asset.apply { onPutInStorage(key, this@AssetsStorage) }
    }

    fun removeAsset(name: String) {
        assets.remove(name)
    }

    fun <T : Asset> getAsset(name: String): T? {
        return assets[name] as? T
    }
}