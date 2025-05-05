package org.qbrp.core.assets

import kotlinx.io.files.FileNotFoundException
import org.qbrp.core.assets.common.Asset
import org.qbrp.core.assets.common.AssetLifecycleManager
import org.qbrp.core.assets.common.AssetsStorage
import org.qbrp.core.assets.common.Key
import org.qbrp.core.assets.common.files.FileReference
import java.io.File

object ServerAssets: AssetsAPI {
    val storage = AssetsStorage()
    val lifecycleManager = AssetLifecycleManager(storage)

    override fun register(key: Key, asset: Asset) {
        storage.addAsset(key.getDir(), asset)
    }

    override fun register(key: String, asset: Asset) {
        storage.addAsset(key, asset)
    }

    override fun <T : Asset> load(fileReference: FileReference<T>): T {
        if (!fileReference.exists()) throw FileNotFoundException("Файл ${fileReference.key.getDir()} не существует")
        return fileReference.read().also {
            register(fileReference.getKey(), it)
        }
    }

    override fun <T : Asset> getByKey(key: Key): T? {
        return storage.getAsset<T>(key.value)
    }
}