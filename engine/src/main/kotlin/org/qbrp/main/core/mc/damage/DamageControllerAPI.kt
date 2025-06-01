package org.qbrp.main.core.mc.damage

import org.qbrp.main.core.modules.ModuleAPI

interface DamageControllerAPI: ModuleAPI {
    fun isEnabled(): Boolean
}