package org.qbrp.engine.time.config

import org.qbrp.core.assets.common.Asset
import org.qbrp.core.assets.common.NamedAsset
import org.qbrp.engine.time.Period

class PeriodsConfig(val periods: List<Period> = emptyList()): NamedAsset() {
    override val name: String = "config.yml"
}