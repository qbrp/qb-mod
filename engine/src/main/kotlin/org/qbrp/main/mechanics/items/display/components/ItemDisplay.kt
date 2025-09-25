package org.qbrp.main.mechanics.items.display.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.networking.synchronization.SynchronizerComponent
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.serialization.ContainerDisplay
import org.qbrp.main.engine.controls.entity.display.IconItemModelId
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.items.components.ItemComponent
import org.qbrp.main.mechanics.items.display.NameData
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.display.InteractionIcon
import org.qbrp.main.engine.controls.entity.display.InteractableName
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.items.networking.SyncListener
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.components.LiteralTextComponent
import org.qbrp.main.engine.text.literal
import org.qbrp.main.mechanics.ItemModelId
import org.qbrp.main.mechanics.items.display.ItemDisplayPacket
import org.qbrp.main.mechanics.items.display.PlaceholdersStorage
import org.qbrp.main.mechanics.items.display.PlainTextTooltip

/**
 * Отправляет клиенту данные о названии предмета, его модели и статических описаниях, собирая их в getName() и generateDescriptions().
 * Динамические описания собираются вручную на клиент
 * **/
class ItemDisplay(ctx: ItemComponentInstantiateContext, var model: ItemModelId): ItemComponent(ctx), SyncListener, PlaceholdersStorage, ContainerDisplay {
    @Serializable data class Data(val model: String): DTO
    override val dto: DTO? get() = Data(model.modelId)
    override val placeholders: MutableMap<String, String> = mutableMapOf()

    override val containerDisplayName: ContainerDisplayName
        get() = ContainerDisplayName(getName())

    fun getName(): String {
        return get<NameData>()?.text ?: "Безымянный предмет"
    }

    fun getDescription(player: ServerPlayerObject): List<TextComponent> {
        val base = filterIsInstance<PlainTextTooltip>()
            .map { it.provide(player, serverItem) }
        val meta = get<Meta>()?.let { listOf(LiteralTextComponent(it.key, Style(TextColor.GRAY))) } ?: emptyList()
        return base + meta
    }

    fun getInteractableDisplay(): InteractableViewData {
        return InteractableViewData.of(
            literal(getName()),
            InteractionIcon.Item(IconItemModelId(model.modelId)),
        )
    }

    override fun onSync(
        synchronizer: SynchronizerComponent<*>,
        player: ServerPlayerObject
    ) {
        synchronizer.sendPacket(createPacket(player), CHANNEL, player)
    }

    private fun createPacket(player: ServerPlayerObject): ItemDisplayPacket {
        return ItemDisplayPacket(
            item.id,
            model,
            LiteralTextComponent(getName()),
            getDescription(player),
        )
    }

    companion object {
        const val CHANNEL = "item-display"
    }
}