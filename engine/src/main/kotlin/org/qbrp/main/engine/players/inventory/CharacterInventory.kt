package org.qbrp.main.engine.players.inventory

import kotlinx.coroutines.withContext
import org.qbrp.main.core.CoreContext
import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.util.containerComponentName
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.Capacity
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.inventory.player.PlayerInventoryManager
import org.qbrp.main.engine.inventory.entity.Inventory
import org.qbrp.main.engine.inventory.entity.InventoryGroup
import org.qbrp.main.engine.inventory.entity.InventoryParams
import org.qbrp.main.engine.inventory.entity.ScreenCorner
import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
import org.qbrp.main.engine.physics.dynamics.manipulate.Manipulator
import org.qbrp.main.engine.players.characters.components.CharacterComponent
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext
import org.qbrp.main.engine.players.inventory.creative.PositionProxy
import org.qbrp.main.engine.players.inventory.hand.CarryVariance
import org.qbrp.main.engine.players.inventory.hand.Hand
import org.qbrp.main.engine.players.inventory.hand.ItemCarrier
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.components.PlayerComponentBehaviour
import org.qbrp.main.engine.players.model.context.PlayerContext

fun handContainerId(side: Hand.Side, characterId: String) = ContainerId("${characterId}.${side.name}")

fun handContainerId(side: Hand.Side, characterId: Int) = handContainerId(side, characterId.toString())

fun equipmentContainerId(characterId: Int) = ContainerId("${characterId}.equipment")

internal fun PlayerComponentBehaviour.instantiateHand(ctx: CharacterComponentInstantiateContext, side: Hand.Side): Hand =
    with(serverPlayer.context) {
        val id = handContainerId(side, ctx.character.id.toString())
        containerService.newContainer(
            name = ContainerDisplayName(side.displayName),
            capacity = Capacity.FixedMass(10f),
            containerId = id
        )
        return serverPlayer.addComponent(Hand(ctx, side, id, containerService, manipulator), name = "Hand#${side.id}")
    }

val Hand.Side.containerName get() = ContainerDisplayName(displayName)

class CharacterInventory(
    ctx: CharacterComponentInstantiateContext,
    private val containerService: ContainerService,
    private val inventoryManager: PlayerInventoryManager
) : CharacterComponent(ctx), ItemCarrier, Activateable {
    override val owner: ServerPlayerObject get() = serverPlayer
    override val variances: Collection<CarryVariance> get() = hands

    val equipment = Equipment()

    val rightHand = instantiateHand(ctx, Hand.Side.RIGHT)
    val leftHand = instantiateHand(ctx, Hand.Side.LEFT)
    val hands get() = filterIsInstance<Hand>()

    inner class Equipment() {
        val container = containerService.newContainer(
            name = ContainerDisplayName("Снаряжение"),
            capacity = Capacity.Infinity,
            containerId = equipmentContainerId(character.id)
        )
    }

    override fun onDisable() {
        getEntries().forEach {
            it.add(PositionProxy(serverPlayer.context.world, Vec3Adapter()))
        }
    }

    context(ctx: CoreContext)
    fun openHands() {
        inventoryManager.openGroup(
            InventoryGroup(
                hands.map { Inventory(it.id, InventoryParams.DEFAULT) },
                ScreenCorner.BOTTOM_LEFT
            )
        )
    }

    context(ctx: CoreContext)
    fun close() {
        inventoryManager.openedGroups.forEach { group ->
            getContainers().forEach { container ->
                if (group.inventories.map { it.containerId }.contains(container)) {
                    inventoryManager.closeGroup(group)
                }
            }
        }
    }

    private fun addAnchor(id: ContainerId) {
        player.getOrAdd(name = containerComponentName(id)) { Container(id) }
    }

    private fun getContainer(id: ContainerId): Container? {
        return getComponentAs<Container>(containerComponentName(id))
    }

    private fun containerExists(id: ContainerId): Boolean {
        return getContainer(id) != null
    }

    fun getEntries(): List<ContainerEntry> = getContainers().flatMap { containerService.getItems(it) }

    fun getContainers(): List<ContainerId> = listOf(rightHand.id, leftHand.id, equipment.container)
}