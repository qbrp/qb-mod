package org.qbrp.core.assets

import org.qbrp.core.assets.common.Asset
import org.qbrp.core.assets.common.AssetKey
import org.qbrp.core.assets.common.Key
import org.qbrp.core.assets.common.Repository
import org.qbrp.core.assets.common.files.FileReference

interface AssetsAPI: Repository {
    fun register(key: Key, asset: Asset)
    fun register(key: String, asset: Asset)
    fun <T: Asset> load(fileReference: FileReference<T>): T
    fun <T : Asset> loadOrCreate(asset: T, fileReference: FileReference<T>): T
    fun <T: Asset> getOrLoad(fileReference: FileReference<T>): T
}