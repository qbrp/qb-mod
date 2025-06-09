package org.qbrp.client.engine.inventory.model

import net.minecraft.item.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.client.render.hud.InventoryManager
import org.qbrp.client.render.inventory.InventoryWidget
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.model.StateEntry
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.engine.items.components.ItemBehaviour
import org.qbrp.main.core.synchronization.components.InternalMessageReceiver
import org.qbrp.main.core.utils.format.Format.asMiniMessage

interface AbstractContainerHolder : ContainerHolder, ItemProvider, InternalMessageReceiver, KoinComponent, StateEntry  {
    var entries: List<ClientInventoryEntry>
    override fun setItems(items: List<ClientInventoryEntry>) {
        this.entries = items
    }

    override fun provideStacks(): List<ItemStack> {
        return entries.map { it.asItemStack() }
    }

    override fun onMessage(id: String, content: ClusterViewer) {
        if (id == "inventory.${this.id}.open") {
            get<InventoryManager>().open(
                InventoryWidget(
                    "Test Container".asMiniMessage(),
                    requireState().getComponent<ActionHandler>()!!,
                    this,
                    get(),
                    this.id)
            )
        }
        if (id == "inventory.${this.id}.close") {
            get<InventoryManager>().close(this.id)
        }
    }
}