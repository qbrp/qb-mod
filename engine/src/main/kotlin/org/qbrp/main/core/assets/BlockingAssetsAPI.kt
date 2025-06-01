package org.qbrp.main.core.assets

import org.qbrp.main.core.assets.common.Asset
import org.qbrp.main.core.assets.common.Key
import org.qbrp.main.core.assets.common.Repository
import org.qbrp.main.core.assets.common.references.FileReference

interface BlockingAssetsAPI: Repository {
    fun register(key: Key, asset: Asset)
    fun register(key: String, asset: Asset)
    fun <T: Asset> load(fileReference: FileReference<T>): T
    fun <T : Asset> loadOrCreate(asset: T, fileReference: FileReference<T>): T
    fun <T : Asset> getOrCreate(asset: T, fileReference: FileReference<T>): T
    fun <T: Asset> getOrLoad(fileReference: FileReference<T>): T
}