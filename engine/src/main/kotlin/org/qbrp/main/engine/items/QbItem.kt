package org.qbrp.main.engine.items

import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item
import org.koin.core.component.KoinComponent
import org.qbrp.main.engine.Engine
import org.qbrp.main.engine.items.model.ItemStorage
import org.qbrp.main.engine.items.model.ServerItemObject

class QbItem(): Item(ItemsModule.SETTINGS), KoinComponent {
}