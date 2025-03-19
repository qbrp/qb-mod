package org.qbrp.engine.damage

import org.qbrp.system.modules.ModuleAPI

interface DamageControllerAPI: ModuleAPI {
    fun isEnabled(): Boolean
}