package org.qbrp.main.engine.controls.model

import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.controls.entity.IconIds
import org.qbrp.main.engine.controls.entity.display.EmptyNode
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.display.InteractionViewNode
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableObject
import org.qbrp.main.engine.controls.entity.interactions.AnyInteraction
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.model.inspect.InspectionInteractions
import org.qbrp.main.engine.controls.model.player.InventoryInteractions
import org.qbrp.main.engine.controls.entity.interactions.AvailableInteraction
import org.qbrp.main.engine.controls.entity.interactions.InteractionKind
import org.qbrp.main.engine.controls.entity.toIcon
import org.qbrp.main.engine.controls.model.container.ContainerInteractable
import org.qbrp.main.engine.controls.model.item.ItemInteractable
import org.qbrp.main.engine.controls.model.player.CarryVarianceInteractable
import org.qbrp.main.engine.controls.model.player.IdentifiableInteractable
import org.qbrp.main.engine.controls.model.player.PropInteractions
import org.qbrp.main.engine.players.inventory.hand.Hand
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.text.toSequenceBuilder

class InteractionNodeComposer(private val containerService: ContainerService) {
    data class Context(val actions: Map<Interactable, List<AvailableInteraction>>, val call: InteractionContext)

    fun composeToNodes(
        actions: Map<InteractableObject, List<AvailableInteraction>>,
        context: InteractionContext
    ): Map<InteractableViewData, List<InteractionViewNode>> {
        val context = Context(actions.mapKeys { it.key.interactable }, context)
        return with(context) {
             getNodes()
        }
    }

    fun AvailableInteraction.isInteraction(i: AnyInteraction): Boolean {
        return interaction.kind == i.kind
    }

    fun List<AvailableInteraction>.filterInteraction(i: AnyInteraction): List<AvailableInteraction> {
        return filter { it.isInteraction(i) }
    }

    context(ctx: Context)
    private fun getNodes(): Map<InteractableViewData, List<InteractionViewNode>> {
        val allInteractions = ctx.actions.flatMap { it.value }
        val objects = ctx.actions.keys

        val putItemInteractions = allInteractions
            .filterInteraction(InventoryInteractions.PUT_ITEM).associateWith { i ->
                val objects = objects
                    .filterIsInstance<IdentifiableInteractable>()
                    .filter { it.id == (i.obj.interactable as ContainerInteractable).anchored }
                objects
            }

        return ctx.actions.toList().associate { (obj, interactions) ->
            val root = mutableListOf<InteractionViewNode>()

            fun List<AvailableInteraction>.mapToNodes(i: AnyInteraction, lazyNode: (AvailableInteraction) -> InteractionViewNode) {
                filterInteraction(i).forEach { i ->
                    root += lazyNode(i)
                }
            }

            val takeItemInteractions = interactions.filterInteraction(InventoryInteractions.TAKE_ITEM)
            val grabItemInteraction = interactions.filterInteraction(PropInteractions.GRAB_PROP).firstOrNull()
            val releaseItemInteraction = interactions.filterInteraction(PropInteractions.RELEASE_PROP).firstOrNull()
            val hasHandInteractions = takeItemInteractions.isNotEmpty()
            if (hasHandInteractions) {
                val handNode = EmptyNode(
                    literal("Рука"),
                    IconIds.HAND.toIcon(),
                    children = handNodes(takeItemInteractions, grabItemInteraction, releaseItemInteraction)
                )
                root += handNode
            }

            val copyInteraction = interactions.filterInteraction(InventoryInteractions.COPY_ITEM)
            copyInteraction.forEach { i ->
                root += InteractionViewNode(
                    i.identity,
                    literal("Копировать"),
                    IconIds.COPY.toIcon()
                )
            }

            val identifiable = obj is IdentifiableInteractable
            if (identifiable) {
                val putItem = allInteractions.filter { putItemInteractions[it]?.any { it.id == obj.id } == true }
                if (putItem.isNotEmpty()) {
                    root += EmptyNode(literal("Положить предмет"), children = containersToPut(putItem))
                }
            }

            interactions.mapToNodes(InspectionInteractions.CHARACTER) { i ->
                InteractionViewNode(i.identity, literal("Осмотреть"))
            }

            interactions.mapToNodes(InventoryInteractions.DELETE_ITEM) { i ->
                InteractionViewNode(i.identity, literal("Удалить"), IconIds.TRASH_CAN.toIcon())
            }

            interactions.mapToNodes(InventoryInteractions.SEE_INVENTORIES) { i ->
                InteractionViewNode(i.identity, literal("Открыть инвентарь"), IconIds.BACKPACK.toIcon())
            }

            interactions.mapToNodes(PropInteractions.SPAWN_PROP) { i ->
                InteractionViewNode(i.identity, literal("Выкинуть"), IconIds.THROW_AWAY.toIcon())
            }

            obj.interactableDisplay to root
        }.filter {
            it.value.isNotEmpty()
        }
    }

    context(ctx: Context)
    private fun containersToPut(putItemInteractions: List<AvailableInteraction>): List<InteractionViewNode> {
        return putItemInteractions.map { i ->
            val item = i.subject.interactable as ItemInteractable
            val itemName = item.interactableDisplay.name.text
            val itemIcon = item.interactableDisplay.icon

            InteractionViewNode(
                i.identity,
                itemName,
                itemIcon
            )
        }
    }

    context(ctx: Context)
    private fun handNodes(
        takeItem: List<AvailableInteraction>?,
        grabItem: AvailableInteraction?,
        releaseItem: AvailableInteraction?
    ) : List<InteractionViewNode> {
        val takeItem = takeItem?.let {
            EmptyNode(literal("Взять"), children = carryList(it))
        }
        val grabItem = grabItem?.let {
            InteractionViewNode(it.identity, literal("Тащить"))
        }

        val releaseItem = releaseItem?.let {
            InteractionViewNode(it.identity, literal("Отпустить"))
        }
        return listOfNotNull(takeItem, grabItem, releaseItem)
    }

    context(ctx: Context)
    private fun carryList(carryInteractions: List<AvailableInteraction>): List<InteractionViewNode> {
        return carryInteractions.map { i ->
            val carry = i.subject.interactable as CarryVarianceInteractable
            val name = containerService.getName(carry.id)
            val label = when (name.value) {
                Hand.Side.RIGHT.displayName -> literal("В правую")
                Hand.Side.LEFT.displayName -> literal("В левую")
                else -> name.asLiteral()
            }
            InteractionViewNode(i.identity, label)
        }
    }
}