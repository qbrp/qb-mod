package org.qbrp.client.engine.inventory.model

import org.koin.core.component.KoinComponent
import org.qbrp.client.engine.inventory.ActionMessageSender
import org.qbrp.main.core.game.model.StateEntry
import org.qbrp.main.core.game.model.components.Loadable

interface ContainerHolder: ItemHolder, StateEntry, Loadable, KoinComponent {
    val actionMessageSender: ActionMessageSender get() = getKoin().get()
    override fun onLoad() {
        super<Loadable>.onLoad()
        requireState().apply {
            addComponentIfNotExist(ComponentActionHandler(actionMessageSender))
        }
    }
}