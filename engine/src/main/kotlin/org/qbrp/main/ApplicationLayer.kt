package org.qbrp.main

import org.qbrp.main.core.modules.ModuleManager
import org.qbrp.main.core.util.log.LoggerUtil

open class ApplicationLayer(override val packet: String): ModuleManager() {
    override val logger = LoggerUtil.get(packet.split(".")[3].replaceFirstChar { it.uppercase() })
}