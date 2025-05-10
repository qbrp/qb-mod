package org.qbrp.engine.modules

import org.qbrp.core.mc.registry.CommandsRepository
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule

@Autoload
class ModuleCommands: QbModule("module-commands") {
    override fun load() {
        CommandsRepository.add(StateCommand())
    }
}