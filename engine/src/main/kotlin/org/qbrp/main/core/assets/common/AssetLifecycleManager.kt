package org.qbrp.main.core.assets.common

import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.deprecated.resources.Savable
import org.qbrp.main.core.assets.common.Asset

class AssetLifecycleManager(val storage: AssetsStorage): Lifecycle<Asset> {
    override fun onCreated(obj: Asset) = Unit

    override fun unload(obj: Asset) {
        storage.removeAsset(obj.key)
    }

    override fun save(obj: Asset) {
        if (obj is Savable) obj.save()
    }
}