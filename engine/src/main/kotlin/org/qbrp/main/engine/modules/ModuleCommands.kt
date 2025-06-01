package org.qbrp.main.engine.modules

import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload
class ModuleCommands: QbModule("module-commands") {
    override fun onLoad() {
        get<CommandsAPI>().add(ModuleCommand())
    }
}