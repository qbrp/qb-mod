package org.qbrp.main.engine.items.components.meta

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.components.ContainedIn
import org.qbrp.main.engine.items.components.ItemComponent
import org.qbrp.main.engine.items.components.ItemComponentInstantiateContext
import org.qbrp.main.engine.world.location.Position
import org.qbrp.main.engine.props.components.SpawnedProp
import org.qbrp.main.engine.world.location.Location
import org.qbrp.main.engine.world.model.World

class ItemLocationMediator(ctx: ItemComponentInstantiateContext, private val containerService: ContainerService) : ItemComponent(ctx), ItemLocation {
    override val pos: Position get() = getActualLocationComponent().pos
    override val world: World get() = getActualLocationComponent().world

    private fun getActualLocationComponent(): Location {
        return get<SpawnedProp>()
            ?: get<ContainedIn>()?.let { containerService.getAnchoredEntity(it.id)?.get<Location>() }
            ?: getComponents().filter { it !is ItemLocationMediator }.filterIsInstance<Location>().firstOrNull()?.let { return it }
            ?: throw IllegalStateException("Не найден компонент трансформации предмета")
        }
}