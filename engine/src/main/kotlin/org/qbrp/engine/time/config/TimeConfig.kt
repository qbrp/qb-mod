package org.qbrp.engine.time.config

import org.qbrp.core.assets.common.NamedAsset
import org.qbrp.system.modules.MainConfigAsset

data class TimeConfig(val formatDo: String = "&6&l{time}",
                      val doFrequency: Int = 2,
                      val sendNotificationsOnNewPeriod: Boolean = true,
                      val rpTimeOffset: Int = 0
): MainConfigAsset()