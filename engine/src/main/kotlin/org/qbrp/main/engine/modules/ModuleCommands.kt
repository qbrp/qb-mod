package org.qbrp.main.engine.modules

import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload
class ModuleCommands: EngineModule("module-commands") {
    override fun onLoad() {
        get<CommandsAPI>().add(ModuleCommand())
    }
}