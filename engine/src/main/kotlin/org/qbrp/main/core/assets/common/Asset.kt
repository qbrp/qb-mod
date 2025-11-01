package org.qbrp.main.core.assets.common

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Transient
import org.qbrp.main.core.components.objects.BaseEntity
import org.qbrp.main.core.util.IDGenerator

abstract class Asset() : BaseEntity<Asset>(IDGenerator.nextId().toString(),) {
    @JsonIgnore @Transient lateinit var key: String
        private set
    fun onPutInStorage(key: String, storage: AssetsStorage) {
        this.key = key
    }
}
