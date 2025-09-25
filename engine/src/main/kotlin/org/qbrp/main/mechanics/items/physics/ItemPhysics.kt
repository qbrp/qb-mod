package org.qbrp.main.mechanics.items.physics

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.toDTO
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.state.StateJsonField
import org.qbrp.main.engine.items.components.ItemComponent
import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.props.components.Dimensions
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext
import org.qbrp.main.engine.physics.dynamics.shapes.toShape
import org.qbrp.main.engine.physics.model.MaterialManager
import org.qbrp.main.engine.physics.model.agent.Solid
import org.qbrp.main.engine.physics.model.VolumetricShape

class ItemPhysics(
    ctx: ItemComponentInstantiateContext,
    override val materialManager: MaterialManager = MaterialManager(),
    override val externalState: ComponentManager = ctx.componentManager,
    val dimensions: Dimensions = externalState.get<Dimensions>() ?: throw Exception("Не указаны габариты предмета"),
    override var temperature: Double = 298.15
): ItemComponent(ctx), Solid, StateTick {
    override val displayName: String get() = get<Meta>()?.key ?: "Неизвестное вещество"
    override val shape: VolumetricShape = dimensions.toShape()

    init { materialManager.parent = this }

    override fun tickState() {
        materials.forEach { it.tickState() }
    }

    //override fun asProp(): ServerItemObject = serverItem

    @Serializable data class Data(val materials: StateJsonField,
                                  val mass: Double? = null,
                                  val dimensions: Dimensions? = null,
                                  val temperature: Double? = null,
                                  val densityProportion: Double = 1.0): DTO
    override val dto: DTO?
        get() = Data(materialManager.toDTO(), mass, dimensions, temperature)
}