package org.qbrp.main.engine.time.config

import org.qbrp.main.core.assets.common.NamedAsset
import org.qbrp.main.core.modules.MainConfigAsset

data class TimeConfig(val formatDo: String = "&6&l{time}",
                      val doFrequency: Int = 2,
                      val sendNotificationsOnNewPeriod: Boolean = true,
                      val rpTimeOffset: Int = 0
): MainConfigAsset()