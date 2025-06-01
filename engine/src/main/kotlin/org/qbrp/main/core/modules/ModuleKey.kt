package org.qbrp.main.core.modules

import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.Key

class ModuleKey(val module: QbModule, val relative: String): Key {
    override fun getId(): String {
        return "${FileSystem.MODULES.path}/${module.getName()}/$relative"
    }
}