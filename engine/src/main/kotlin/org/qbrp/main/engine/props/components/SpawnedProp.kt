package org.qbrp.main.engine.props.components

import org.qbrp.main.engine.items.components.ItemComponent
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext
import org.qbrp.main.engine.physics.system.entity.Body
import org.qbrp.main.engine.physics.system.entity.PhysicsRotation
import org.qbrp.main.engine.world.location.WorldPos
import org.qbrp.main.engine.world.location.asWorld
import org.qbrp.main.engine.world.model.World

class SpawnedProp(
    ctx: ItemComponentInstantiateContext,
    override val world: World,
    val body: Body
) : ItemComponent(ctx), ItemTransform {
    override val pos: WorldPos get() = body.transform.pos.asWorld()
    override val quat: PhysicsRotation get() = body.transform.rot
}