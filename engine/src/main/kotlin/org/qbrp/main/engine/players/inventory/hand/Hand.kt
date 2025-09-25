package org.qbrp.main.engine.players.inventory.hand

import org.litote.kmongo.bucket
import org.qbrp.main.core.components.Activateable
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.ContainerAnchor
import org.qbrp.main.engine.containers.v2.system.request.Destination
import org.qbrp.main.engine.containers.v2.system.request.MoveCallback
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.physics.dynamics.manipulate.ManipulationTarget
import org.qbrp.main.engine.physics.dynamics.manipulate.Manipulator
import org.qbrp.main.engine.players.characters.components.CharacterBind
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.engine.props.components.PropManager
import org.qbrp.main.engine.props.components.SpawnedProp
import org.qbrp.main.engine.world.location.Location

/**
 * # Абстрактная рука
 * Основной способ взаимодействия с окружающим миром. Является субъектом и физическим инвентарем.
 * Рука обозначает и другие способы перетаскивать объекты, например, подмышки. Вследствие этого есть возможность поднимать большое количество предметов.
 * **/
class Hand(
    ctx: CharacterComponentInstantiateContext,
    val side: Side,
    override val id: ContainerId,
    private val containerService: ContainerService,
    private val manipulator: Manipulator
) : PlayerComponent(ctx), CharacterBind, CarryVariance, ContainerAnchor, Activateable {
    override var enabled: Boolean = false
    private val itemsInHand get() = containerService.getItems(id)
    private val itemGrabbed: Boolean get() = carriedItems.any { it.grabbed }

    override val carriedItems: List<CarriedItem>
        get() = itemsInHand
            .filterIsInstance<ServerItemObject>()
            .map { CarriedItemImpl(it) }

    override fun held(item: ServerItemObject, callback: MoveCallback) {
        containerService.requestMove(item, Destination.Container(id), callback = callback)
    }

    override fun onDisable() {
        carriedItems.forEach {
            if (it.grabbed) it.release()
        }
    }

    enum class Side(val displayName: String, val id: String) {
        LEFT("Левая рука", "left"),
        RIGHT("Правая рука", "right")
    }

    inner class CarriedItemImpl(override val model: ServerItemObject) : CarriedItem {
        private var grabbedBody: Body? = null
        override val grabbed: Boolean get() = grabbedBody != null

        override fun moveTo(
            container: ContainerId,
            callback: MoveCallback
        ) {
            containerService.requestMove(model, Destination.Container(container), callback = callback)
        }

        override fun release(
            callback: MoveCallback
        ) {
            if (grabbed) {
                manipulator.unpin(grabbedBody!!)
                grabbedBody = null
            }
            moveToWorld(serverPlayer.location, callback)
        }

        override fun grabInWord() {
            if (itemGrabbed) return
            val propManager = model.require<PropManager>()
            propManager.spawn(serverPlayer.location)
            val prop = model.require<SpawnedProp>()
            val body = prop.body
            manipulator.pin(body, ManipulationTarget.Position {
                serverPlayer.entity.eyePos.add(0.0, 4.0, 0.0)
            })
            grabbedBody = body
        }

        override fun returnBack() {
            if (!itemGrabbed) return
            val body = grabbedBody!!
            val propManager = model.require<PropManager>()
            manipulator.unpin(body)
            propManager.despawn()
            grabbedBody = null
        }

        private fun moveToWorld(location: Location, callback: MoveCallback = {}) {
            containerService.requestMove(model, Destination.World(location), callback = callback)
        }
    }
}