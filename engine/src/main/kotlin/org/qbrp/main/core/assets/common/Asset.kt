package org.qbrp.main.core.assets.common

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.qbrp.main.core.Core
import org.qbrp.main.core.game.model.objects.BaseEntity
import org.qbrp.main.core.assets.Assets
import org.qbrp.main.core.assets.common.AssetLifecycleManager
import org.qbrp.main.core.assets.common.AssetsStorage
import org.qbrp.main.core.game.IDGenerator

abstract class Asset() : BaseEntity<Asset>(IDGenerator.nextId().toString(),) {
    @Transient lateinit var key: String
        private set
    fun onPutInStorage(key: String, storage: AssetsStorage) {
        this.key = key
    }
}
