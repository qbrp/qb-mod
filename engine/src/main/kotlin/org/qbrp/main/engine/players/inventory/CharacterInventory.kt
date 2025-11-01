package org.qbrp.main.engine.players.inventory

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.getAs
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.components.extensions.has
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.util.containerComponentName
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.components.AcceptResolver
import org.qbrp.main.engine.containers.components.Capacity
import org.qbrp.main.engine.containers.components.Container
import org.qbrp.main.engine.containers.components.ContainerDisplayName
import org.qbrp.main.engine.containers.components.Rules
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.event.intentCloseContainer
import org.qbrp.main.engine.containers.event.openContainer
import org.qbrp.main.engine.containers.system.request.Destination
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.physics.system.service.ManipulatorSystem
import org.qbrp.main.engine.players.characters.components.CharacterComponent
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext
import org.qbrp.main.engine.players.characters.components.hasCharacter
import org.qbrp.main.engine.players.characters.components.sendCharacterNotAppliedNotification
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.PlayerComponentBehaviour
import org.qbrp.main.engine.world.location.WorldVec3
import org.qbrp.main.engine.players.characters.entity.CharacterData
import org.qbrp.main.engine.players.model.world

fun handContainerId(side: Hand.Side, characterId: String) = ContainerId("${characterId}.${side.name}")

fun handContainerId(side: Hand.Side, characterId: Int) = handContainerId(side, characterId.toString())

fun equipmentContainerId(characterId: Int) = ContainerId("${characterId}.equipment")

val equipmentRules = Rules(
    canAccept = AcceptResolver { item, _, _ ->
        item.has<Equip>()
    }
)

fun ContainerService.createEquipmentContainer(character: CharacterData) =
    newContainer(
        name = ContainerDisplayName("Экипировка"),
        capacity = Capacity.Infinity,
        containerId = equipmentContainerId(character.id),
        rules = equipmentRules
    )

fun ContainerService.createHandContainer(character: CharacterData, side: Hand.Side) =
    newContainer(
        name = side.containerName,
        capacity = Capacity.FixedMass(10f),
        containerId = handContainerId(side, character.id),
    )

internal fun PlayerComponentBehaviour.instantiateHand(
    ctx: CharacterComponentInstantiateContext,
    container: ContainerId,
    side: Hand.Side,
    containerService: ContainerService = GlobalContext.get().get(),
    manipulator: ManipulatorSystem = GlobalContext.get().get()
): Hand {
    return serverPlayer.addComponent(
        Hand(ctx, side, container, containerService, manipulator), name = "Hand#${side.id}"
    )
}

val ContainerId.isEquipment get() = raw.split(".").getOrNull(1) == "equipment"

val Hand.Side.containerName get() = ContainerDisplayName(displayName)

data class CharacterInventoryContainers(
    val rightHand: ContainerId,
    val leftHand: ContainerId,
    val equipment: ContainerId
)

fun ServerPlayerObject.openCharacterInventory() {
    if (!hasCharacter()) sendCharacterNotAppliedNotification("Открытие инвентаря")
    else require<CharacterInventory>().open()
}

class CharacterInventory(
    ctx: CharacterComponentInstantiateContext,
    containers: CharacterInventoryContainers,
    private val containerService: ContainerService,
) : CharacterComponent(ctx), ItemCarrier, Activateable {
    override val owner: ServerPlayerObject get() = serverPlayer
    override val variances: Collection<CarryVariance> get() = hands

    val equipment = containers.equipment
    val rightHand = instantiateHand(ctx, containers.rightHand, Hand.Side.RIGHT)
    val leftHand = instantiateHand(ctx, containers.leftHand, Hand.Side.LEFT)
    val hands get() = filterIsInstance<Hand>()

    override fun onLeave() {
        getEntries().forEach {
            it.add(PositionProxy(serverPlayer.world, WorldVec3()))
        }
    }

    fun open() = serverPlayer.apply {
        hands.forEach { openContainer(it.id) }
        openContainer(equipment)
    }

    fun close() = serverPlayer.apply {
        getContainers().forEach { intentCloseContainer(it) }
    }

    override fun equip(item: ServerItemObject) {
        containerService.requestMove(
            item,
            Destination.Container(equipment),
        )
    }

    private fun addAnchor(id: ContainerId) {
        player.getOrAdd(setName = containerComponentName(id)) { Container(id) }
    }

    private fun getContainer(id: ContainerId): Container? {
        return getAs<Container>(containerComponentName(id))
    }

    private fun containerExists(id: ContainerId): Boolean {
        return getContainer(id) != null
    }

    fun getEntries(): List<ContainerEntry> = getContainers().flatMap { containerService.getItems(it) }

    fun getContainers(): List<ContainerId> = listOf(rightHand.id, leftHand.id, equipment)
}