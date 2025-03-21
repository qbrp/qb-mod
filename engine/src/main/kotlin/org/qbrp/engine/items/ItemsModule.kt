package org.qbrp.engine.items

import org.koin.core.component.get
import org.koin.dsl.module
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.engine.items.model.ItemLoader
import org.qbrp.engine.items.model.ItemManager
import org.qbrp.engine.items.storage.ItemCommand
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule

@Autoload
class ItemsModule: QbModule("items") {
    override fun load() {
        CommandsRepository.add(get<ItemCommand>())
    }

    override fun getKoinModule() = module {
        single { ItemManager() }
        single { ItemLoader() }
        single { ItemCommand(get()) }
    }
}