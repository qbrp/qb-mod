package org.qbrp.main.engine.items.service

import org.qbrp.main.core.game.storage.TableRepository
import org.qbrp.main.core.storage.TableAccess
import org.qbrp.main.engine.items.ItemsModule
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.model.ItemInstantiateContext

class ItemRepository(val module: ItemsModule,
                     table: TableAccess,
                     factory: ItemFactory,
                     override val cache: ItemStorage<ServerItemObject>
): TableRepository<ServerItemObject, ItemInstantiateContext>(table, factory, cache)