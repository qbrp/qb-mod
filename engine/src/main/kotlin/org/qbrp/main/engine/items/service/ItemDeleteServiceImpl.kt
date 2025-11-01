package org.qbrp.main.engine.items.service

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.containers.api.ContainerService
import org.qbrp.main.engine.containers.system.request.Destination
import org.qbrp.main.engine.containers.system.request.MoveMode
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.physics.system.service.ManipulatorSystem
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.engine.props.components.PropManager
import org.qbrp.main.engine.props.components.SpawnedProp

class ItemDeleteServiceImpl(
    private val containerService: ContainerService,
    private val playerStorage: ServerPlayerStorage,
    private val manipulator: ManipulatorSystem,
    private val itemStorage: ItemStorage<ServerItemObject>,
) : ItemDeleteService {
    override fun delete(item: ServerItemObject) {
        containerService.requestMove(
            item,
            destination = Destination.Void,
            moveMode = MoveMode.Transfer,
            required = true
        )

        item.get<SpawnedProp>()?.let { manipulator.unpin(it.body) }
        item.get<PropManager>()?.despawn()

        itemStorage.remove(item.id)
    }
}