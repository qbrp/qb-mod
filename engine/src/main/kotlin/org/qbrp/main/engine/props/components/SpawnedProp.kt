package org.qbrp.main.engine.props.components

import org.qbrp.main.engine.items.components.ItemComponent
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext
import org.qbrp.main.engine.physics.dynamics.dataformats.Body
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.physics.dynamics.dataformats.Quat
import org.qbrp.main.engine.world.model.World

class SpawnedProp(ctx: ItemComponentInstantiateContext, override val world: World, val body: Body): ItemComponent(ctx), ItemTransform {
    override val pos: Position get() = body.transform.pos
    override val quat: Quat get() = body.transform.quat
}