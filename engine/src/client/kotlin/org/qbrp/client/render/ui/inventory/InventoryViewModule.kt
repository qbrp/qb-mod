package org.qbrp.client.render.ui.inventory

import net.fabricmc.api.EnvType
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.controls.INVENTORY_SELECT
import org.qbrp.client.controls.ITEM_SELECT
import org.qbrp.client.core.mc.connection.DisconnectEvent
import org.qbrp.client.engine.inventory.events.InventoryCloseEvent
import org.qbrp.client.engine.inventory.events.InventoryOpenEvent
import org.qbrp.client.render.ui.inventory.components.SlotGrid
import org.qbrp.client.render.ui.interaction.event.ItemSelectionListener
import org.qbrp.client.render.ui.inventory.components.ExecuteInteractionClickListener
import org.qbrp.client.render.ui.inventory.components.ShowTooltipSlotHoverListener
import org.qbrp.client.render.ui.inventory.service.InventoryViewFactory
import org.qbrp.client.render.ui.inventory.service.InventoryViewManager
import org.qbrp.client.render.ui.view.ScreenEventHandler
import org.qbrp.client.render.ui.withUiContext
import org.qbrp.main.core.events.EventBus
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule

@Autoload(env = EnvType.CLIENT)
class InventoryViewModule: EngineModule("inventory-render") {
    override fun getKoinModule(): Module = module {
        single { InventoryViewManager(get(), get(), get()) }
        single { ShowTooltipSlotHoverListener(get(), get()) }
        single { ExecuteInteractionClickListener(get()) }
        single {
            InventoryViewFactory(
                get<ShowTooltipSlotHoverListener>(),
                get<ExecuteInteractionClickListener>(),
                get(),
                get()
            )
        }
    }

    override fun onEnable() {
        val inventories = get<InventoryViewManager>()
        withUiContext {
            on<InventoryOpenEvent> { inventories.onOpen(containers, screenCorner) }
            on<InventoryCloseEvent> { inventories.onClose(container) }
            on<DisconnectEvent> { inventories.closeAll() }
        }

        get<EventBus>().subscribe(
            ItemSelectionListener(
                ITEM_SELECT,
                ScreenEventHandler(
                    get(),
                    SlotGrid::class
                ) { component, event -> component }
            )
        )
        on(INVENTORY_SELECT) { inventories }
    }
}