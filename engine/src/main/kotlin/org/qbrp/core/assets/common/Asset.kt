package org.qbrp.core.assets.common

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.core.game.model.objects.BaseEntity
import org.qbrp.core.assets.Assets

abstract class Asset(@JsonIgnore override val name: String) : BaseEntity<Asset>(name, Assets.lifecycleManager) {
}
