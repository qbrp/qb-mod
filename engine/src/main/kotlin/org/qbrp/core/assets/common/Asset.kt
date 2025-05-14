package org.qbrp.core.assets.common

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.core.game.model.objects.BaseEntity
import org.qbrp.core.assets.Assets
import org.qbrp.core.game.IDGenerator

abstract class Asset() : BaseEntity<Asset>(IDGenerator.nextId(), Assets.lifecycleManager) {
    @JsonIgnore lateinit var key: String
        private set
    fun onPutInStorage(key: String, storage: AssetsStorage) {
        this.key = key
    }

}
