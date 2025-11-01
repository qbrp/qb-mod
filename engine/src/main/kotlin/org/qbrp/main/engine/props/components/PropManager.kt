package org.qbrp.main.engine.props.components

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.has
import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.items.components.ItemComponent
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext
import org.qbrp.main.engine.physics.system.service.BodyFactory
import org.qbrp.main.engine.physics.system.service.WorldBodyManager
import org.qbrp.main.engine.chemistry.params.PhysicalParameters
import org.qbrp.main.engine.controls.model.item.ItemInteractable
import org.qbrp.main.engine.props.PropDescriptor
import org.qbrp.main.engine.world.location.Location

class PropManager(
    ctx: ItemComponentInstantiateContext,
    private val bodyPropManager: WorldBodyManager,
    private val bodyFactory: BodyFactory
) : ItemComponent(ctx), KoinComponent {
    val isSpawned: Boolean
        get() = has<SpawnedProp>()


    fun spawn(location: Location) {
        if (isSpawned) return

        val descriptor = PropDescriptor(item, location.pos)
        val body = bodyFactory.build(descriptor)
        bodyPropManager.addBody(body, true)
        add(SpawnedProp(serverItem.state.componentContext, location.world, body))
    }

    fun despawn() {
        if (!isSpawned) return

        val prop = require<SpawnedProp>()
        bodyPropManager.destroyBody(prop.body)
        remove<SpawnedProp>()
    }

    companion object {
        fun ItemInteractable.propSpawnAvailable(): Boolean {
            return logic.has<PhysicalParameters>() && logic.propShape != null
        }
    }
}