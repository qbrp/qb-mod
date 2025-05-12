package org.qbrp.system.modules

import org.qbrp.core.assets.FileSystem
import org.qbrp.core.assets.common.Key

class ModuleKey(val module: QbModule, val relative: String): Key {
    override fun getId(): String {
        return "${FileSystem.MODULES.path}/${module.getName()}/$relative"
    }
}