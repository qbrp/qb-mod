package org.qbrp.main.engine.players.physics

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.physics.dynamics.shapes.toShape
import org.qbrp.main.engine.physics.model.MaterialManager
import org.qbrp.main.engine.props.components.Dimensions
import org.qbrp.main.engine.physics.model.agent.Solid
import org.qbrp.main.engine.physics.model.VolumetricShape
import org.qbrp.main.engine.physics.model.params.MassContributor
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext
import org.qbrp.main.mechanics.characters.model.CharacterData
import org.qbrp.main.mechanics.characters.physical.CharacterPhysicalParameters
import javax.naming.OperationNotSupportedException

class PlayerAgent(ctx: PlayerComponentInstantiateContext,
                  override val materialManager: MaterialManager = MaterialManager(),
                  override var temperature: Double = 35.0
) : PlayerComponent(ctx), Solid, MassContributor {
    override val displayName: String get() = player.username.value
    override val externalState: ComponentManager get() = serverPlayer
    override val shape: VolumetricShape = Dimensions(0.6, 0.4, 1.7).toShape()

    override fun calculateMass(): Double = get<CharacterPhysicalParameters>()?.mass ?: CharacterData.AVERAGE_MASS
}