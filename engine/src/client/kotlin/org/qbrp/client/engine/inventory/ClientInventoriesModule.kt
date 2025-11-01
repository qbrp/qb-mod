package org.qbrp.client.engine.inventory

import net.fabricmc.api.EnvType
import org.koin.core.component.inject
import org.qbrp.client.core.getClientPlayer
import org.qbrp.client.core.mc.connection.DisconnectEvent
import org.qbrp.client.deprecated.HeldItem
import org.qbrp.client.deprecated.HeldItemManager
import org.qbrp.client.engine.inventory.service.ContainerStates
import org.qbrp.client.engine.inventory.service.InventoryCloseListener
import org.qbrp.client.engine.inventory.service.InventoryHoldersStorage
import org.qbrp.client.engine.inventory.service.InventoryManager
import org.qbrp.client.engine.inventory.service.InventoryManagerImpl
import org.qbrp.client.engine.players.ClientCharactersBank
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.componentRegistrar
import org.qbrp.main.core.events.on
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.players.inventory.Hand
import org.qbrp.main.engine.players.inventory.ITEM_LIST_CONTAINER_ID
import org.qbrp.main.engine.players.inventory.handContainerId
import org.qbrp.main.engine.players.characters.entity.CharacterData

@Autoload(priority = Priorities.LOWEST + 2, env = EnvType.CLIENT)
class ClientInventoriesModule : EngineModule("inventories"), ClientInventory {
    override fun getKoinModule() = innerWithApi({
        single<HeldItem> { HeldItemManager() }
        single { InventoryHoldersStorage() }
        single { ContainerStates() }
        single<InventoryManager> { getLocal<InventoryManagerImpl>() }
        single { InventoryCloseListener(getLocal()) }
        single<ClientInventory> { this@ClientInventoriesModule }
    }, {
        scoped { InventoryManagerImpl() }
    })

    override val manager by inject<InventoryManager>()
    private val characterBank by inject<ClientCharactersBank>()
    private val containerStates: ContainerStates by inject()

    override fun onEnable() {
        on<DisconnectEvent> { manager.closeAll() }

        componentRegistrar { use<HandItemsComponent>() }
    }

    private val rightHandId get() = handContainerId(Hand.Side.RIGHT,characterBank.character!!.id)
    private val leftHandId get() = handContainerId(Hand.Side.LEFT,characterBank.character!!.id)

    override fun createCharacterInventories(character: CharacterData) {
        val player = getClientPlayer() ?: return
        player.add(
            HandItemsComponent(
                player.componentContext,
                containerStates.add(rightHandId),
                containerStates.add(leftHandId)
            )
        )
    }

    override fun createInventories() {
        containerStates.getOrAdd(ITEM_LIST_CONTAINER_ID)
    }
}