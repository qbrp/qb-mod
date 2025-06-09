package org.qbrp.main.engine.items.components.tooltip.impl

import org.qbrp.main.core.mc.player.ServerPlayerObject
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.engine.anticheat.StringListContent
import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.engine.items.components.tooltip.NameData
import org.qbrp.main.engine.items.components.tooltip.StaticTooltipComponent
import org.qbrp.main.core.synchronization.SynchronizeConvertible

/**
 * Отправляет клиенту данные о названии предмета и статических описаниях, собирая их в getName() и generateDescriptions().
 * Динамические описания собираются вручную на клиенте
 * **/
class ItemDisplay(): ItemBehaviour(), SynchronizeConvertible {
    private fun getName(playerObject: ServerPlayerObject): String {
        return requireState().getComponent<NameData>()?.getName(playerObject) ?: "Безымянный предмет"
    }

    private fun generateDescription(player: ServerPlayerObject): List<String> {
        return requireState().getComponentsIsInstance<StaticTooltipComponent>().map { item.substitutePlaceholders(it.provide(player, item)) }
    }

    override fun toCluster(player: ServerPlayerObject): Cluster {
        val lines = generateDescription(player)
        return ClusterBuilder()
            .component("display.name", getName(player))
            .component("display.lines", StringListContent().apply { setData(lines) })
            .build()
    }
}