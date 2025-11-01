package engine.interactions.player

import core.PlayerTestEnv
import core.TestEnv
import engine.container.TestItem
import engine.container.TestPlayer
import io.mockk.every
import io.mockk.mockk
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.players.model.PlayerContext
import core.randomMinecraftPlayerId
import core.randomStringId
import engine.interactions.DummyLookup
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotHaveSize
import io.mockk.spyk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.engine.containers.v2.api.ContainerService
import org.qbrp.main.engine.containers.v2.system.request.ContainerEntryPrototype
import org.qbrp.main.engine.controls.dsl.InteractionDslContext
import org.qbrp.main.engine.controls.entity.call.InteractionRequestContext
import org.qbrp.main.engine.controls.model.InteractionService
import org.qbrp.main.engine.controls.model.player.InventoryInteractions
import org.qbrp.main.engine.controls.service.activation.InteractionActivation
import org.qbrp.main.engine.controls.system.lookup.InventoryLookup
import org.qbrp.main.engine.inventory.player.PlayerInventoryManager
import org.qbrp.main.engine.inventory.player.impl.ServerPlayerInventoryManager
import org.qbrp.main.engine.items.model.ItemStateImpl
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext
import org.qbrp.main.engine.players.inventory.CharacterInventory
import org.qbrp.main.engine.players.inventory.ItemCarrier

class PlayerInteractionsTest: TestEnv() {
    private lateinit var componentRegistry: ComponentRegistry
    private lateinit var permissionService: PermissionService
    private lateinit var koin: KoinApplication

    @BeforeEach
    fun setup() {
        permissionService = mockk()
        every { permissionService.isOp(any()) } returns true
        componentRegistry = setupComponentsRegistry {
            register { componentClass(CharacterInventory::class) }
            register { componentClass(ServerPlayerInventoryManager::class) }
        }
        koin = startKoin {
            modules(module {
                single { mockk<ContainerService>(relaxed = true) }
                single { mockk<ContainerEntryPrototype>(relaxed = true) }
                single { InteractionDslContext(permissionService) }
            })
        }
    }

    @AfterEach
    fun teardown() {
        koin.close()
    }

    private fun testPlayer(playerContext: PlayerContext = mockk<PlayerContext>()): TestPlayer {
        val uuid = randomMinecraftPlayerId()
        every { playerContext.uuid } returns uuid
        return TestPlayer(playerContext, uuid)
    }

    private fun testCharacterContext(player: TestPlayer): CharacterComponentInstantiateContext {
        val character = PlayerTestEnv.TEST_CHARACTER
        val characterContext = mockk<CharacterComponentInstantiateContext>()
        every { characterContext.componentManager } returns player.state
        every { characterContext.character } returns character
        return characterContext
    }

    context(registry: ComponentRegistry)
    private fun TestPlayer.addCarrier(ctx: CharacterComponentInstantiateContext,
                                      containerService: ContainerService = mockk(relaxed = true),
                                      inventoryManager: PlayerInventoryManager = mockk(relaxed = true)): ItemCarrier {
        return add<CharacterInventory>(CharacterInventory(ctx, containerService, inventoryManager))
    }

    @Test
    fun lookupFindsInteractionsAndItemAsObjectIfInventoryOpenedAndItemHovered(): Unit = with(componentRegistry) {
        val uuid = randomMinecraftPlayerId()
        val playerContext = mockk<PlayerContext>()
        every { playerContext.uuid } returns uuid
        val player = TestPlayer(playerContext, uuid)
        val item = spyk(TestItem(randomStringId(), ItemStateImpl()))

        val character = PlayerTestEnv.TEST_CHARACTER
        val characterContext = mockk<CharacterComponentInstantiateContext>()
        every { characterContext.componentManager } returns player.state
        every { characterContext.character } returns character

        player.add(CharacterInventory(characterContext, mockk(relaxed = true), mockk(relaxed = true)))

        val interactionContext = mockk<InteractionContext>(relaxed = true)
        val call = mockk<InteractionRequestContext>()
        every { call.inventoryHoveredItem } returns item
        every { call.inventoryOpened } returns true
        every { interactionContext.call } returns call

        val lookup = InventoryLookup(player, false, call)
        val interactionService = spyk(InteractionService(mockk(relaxed = true)))
        interactionService.onPlayerJoin(player)

        val interactableItem = interactionService.getItemInteractable(item)
        every { interactionService.getItemInteractable(item) } returns interactableItem

        lookup.apply {
            lookup(
                interactionService.getPlayerInteractable(player),
                interactionService
            )
            getInteractableSubjects() shouldNotHaveSize 0
            getAnyInteractions() shouldContainAll listOf(InventoryInteractions.PUT_ITEM, InventoryInteractions.COPY_ITEM, InventoryInteractions.TAKE_ITEM)
            getInteractableObjects().map { it.interactable } shouldContain interactableItem
        }
    }

    @Test
    fun activationFindsTakeItemSubinteractions(): Unit = with(newEventContext()) {
        val interactionContext = mockk<InteractionContext>(relaxed = true)
        val call = mockk<InteractionRequestContext>()
        every { call.inventoryOpened } returns true
        every { interactionContext.call } returns call

        val item = spyk(TestItem(randomStringId(), ItemStateImpl()))
        val player = testPlayer()
        val characterContext = testCharacterContext(player)
        with(componentRegistry) {
            player.addCarrier(characterContext)
        }

        val interactionService = spyk(InteractionService(mockk(relaxed = true)))
        interactionService.onPlayerJoin(player)

        val itemInteractable = interactionService.getItemInteractable(item)
        val playerInteractable = interactionService.getPlayerInteractable(player)
        val carryVariances = interactionService.getCarryVariancesInteractables(playerInteractable.interactableDisplay, playerInteractable.carrier)

        val activation = InteractionActivation(interactionContext)
        val lookup = DummyLookup(
            listOf(InventoryInteractions.TAKE_ITEM),
            listOf(itemInteractable),
            listOf(playerInteractable, playerInteractable.carrier) + carryVariances
        )
        activation.apply {
            find(lookup)
            assert(hasInteraction(InventoryInteractions.TAKE_ITEM))
            interactions
                .first()
                .filteredSubinteractions shouldHaveSize 2
            // 2 руки
        }
    }

}