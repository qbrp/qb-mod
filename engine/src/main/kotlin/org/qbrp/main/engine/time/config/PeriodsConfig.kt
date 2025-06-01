package org.qbrp.main.engine.time.config

import org.qbrp.main.core.assets.common.NamedAsset
import org.qbrp.main.engine.time.Period

class PeriodsConfig(val periods: List<Period> = emptyList()): NamedAsset() {
    override val name: String = "config.yml"
}