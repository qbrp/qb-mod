package org.qbrp.core.assets.common

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.core.game.model.objects.BaseEntity
import org.qbrp.core.assets.ServerAssets
import java.io.File

abstract class Asset(key: String) : BaseEntity<Asset>(key, ServerAssets.lifecycleManager) {
}
