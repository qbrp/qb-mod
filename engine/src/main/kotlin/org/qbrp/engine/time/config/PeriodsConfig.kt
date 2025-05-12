package org.qbrp.engine.time.config

import org.qbrp.core.assets.common.Asset
import org.qbrp.engine.time.Period

class PeriodsConfig(val periods: List<Period> = emptyList()): Asset("periods") {
}