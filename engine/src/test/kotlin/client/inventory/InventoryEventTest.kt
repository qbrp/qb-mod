package client.inventory

import core.TestEnv
import io.kotest.assertions.throwables.shouldNotThrow
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.client.engine.interactions.ClientInteractionsModule
import org.qbrp.client.render.ui.inventory.components.SlotGrid
import org.qbrp.client.render.ui.interaction.event.InventorySelection
import org.qbrp.client.render.ui.interaction.event.InventorySelectionCallback
import org.qbrp.client.render.ui.interaction.event.ItemSelection
import org.qbrp.client.render.ui.interaction.event.ItemSelectionListener
import org.qbrp.client.render.ui.view.Screen
import org.qbrp.client.render.ui.view.ScreenEventHandler
import org.qbrp.client.render.ui.view.View
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.events.collectLists

class InventoryEventTest: TestEnv() {
    private lateinit var registry: ComponentRegistry

    @BeforeEach
    fun setup() {
        registry = setupComponentsRegistry {
            register { componentClass(SlotGrid::class) }
        }
    }

    @Test
    fun testGetSelectionItemCallbackPropagates(): Unit = with(registry) {
        val bus = newEventBus()
        val ctx = newEventContext(bus)
        val screen = mockk<Screen>(relaxed = true)
        val view = View().apply { add(SlotGrid(componentContext, mockk(relaxed = true), mockk(relaxed = true))) }
        val children = mutableListOf(view)
        every { screen.children } returns children
        bus.subscribe(
            ItemSelectionListener(
                ClientInteractionsModule.ITEM_SELECT,
                ScreenEventHandler(
                    screen,
                    SlotGrid::class) { component, event -> component }
            )
        )
        shouldNotThrow<Exception> {
            with(ctx) {
                InventorySelectionCallback().collectLists<ItemSelection>()
                InventorySelectionCallback().collectLists<InventorySelection>()
            }
        }
    }
}