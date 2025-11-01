package engine.container

import core.TestEnv
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.util.StringId
import org.qbrp.main.core.events.on
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.game.storage.Storage
import org.qbrp.main.core.networking.sender.ServerPacketSender
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.Capacity
import org.qbrp.main.engine.containers.v2.components.ContainedIn
import org.qbrp.main.engine.containers.v2.components.Container
import org.qbrp.main.engine.containers.v2.components.ContainerDisplayName
import org.qbrp.main.engine.containers.v2.entity.ContainerEntity
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.containers.v2.event.ItemAddedInContainerEvent
import org.qbrp.main.engine.containers.v2.system.ContainerEntityStorage
import org.qbrp.main.engine.containers.v2.system.ContainerSystem
import org.qbrp.main.engine.containers.v2.system.ItemCache
import org.qbrp.main.engine.containers.v2.system.request.Destination
import org.qbrp.main.engine.containers.v2.system.request.EntryMove
import org.qbrp.main.engine.containers.v2.system.ItemCommandBuffer
import org.qbrp.main.engine.containers.v2.system.ItemCommandBufferImpl
import org.qbrp.main.engine.containers.v2.system.TransferResult
import org.qbrp.main.engine.containers.v2.system.TransferSystem
import org.qbrp.main.engine.containers.v2.system.request.ContainerEntryPrototype
import org.qbrp.main.engine.containers.v2.system.request.MoveMode
import org.qbrp.main.engine.inventory.networking.impl.ContainerSynchronizerImpl
import org.qbrp.main.engine.items.model.ItemStateImpl
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage
import org.qbrp.main.engine.physics.model.MaterialManager
import org.qbrp.main.engine.physics.model.agent.Agent
import java.util.LinkedList

class TransferSystemTest: TestEnv() {
    private lateinit var buffer: ItemCommandBuffer
    private lateinit var system: TransferSystem
    private lateinit var registry: ComponentRegistry

    @BeforeEach
    fun setup() {
        buffer = ItemCommandBufferImpl()
        system = TransferSystem(buffer)
        registry = setupComponentsRegistry {
            register { data<ContainedIn>() }
        }
    }

    private fun qOf(vararg m: EntryMove) = LinkedList<EntryMove>().apply { addAll(m) }

    private fun cid(label: String): ContainerId = ContainerId(label)

    context(ctx: ComponentRegistry)
    private fun testItem(mass: Double, volume: Double): TestItem {
        val state = ItemStateImpl()
        val item = TestItem(StringId(IDGenerator.nextIdStr()), state)
        state.parent = item
        state.addComponent(object : Agent, DataComponent {
            override val volume: Double get() = volume
            override val materialManager: MaterialManager
                get() = MaterialManager()
            override val displayName: String
                get() = "Test item"
            override var temperature: Double
                get() = 0.0
                set(value) {}
            override val mass: Double get() = mass
        }, name = "Agent", enable = true)
        return item
    }

    
    fun `container entries sync broadcast when transfer or copy`() = with(registry) {
        val itemStorage = ItemStorage<ServerItemObject>()
        val containerEntities = ContainerEntityStorage().apply { addStorage(itemStorage as Storage<ContainerEntity>) }
        val containerService = ContainerSystem(
            system,
            buffer,
            itemStorage,
            mockk(relaxed = true),
            containerEntities,
            ItemCache(itemStorage)
        )
        val packetSender = mockk<ServerPacketSender>(relaxed = true)
        val synchronizer = ContainerSynchronizerImpl(packetSender, containerService)

        val item1 = testItem(1.0, 0.0)

        val container1 = containerService.newContainer(
            ContainerDisplayName(""),
            Capacity.Infinity,
            listOf(item1)
        )
        val container2 = containerService.newContainer(
            ContainerDisplayName(""),
        )

        val itemContainer = testItem(0.0, 0.0).apply {
            add(Container(container1))
            add(Container(container2))
        }
        itemStorage.add(item1)
        itemStorage.add(itemContainer)

        buffer.remove(item1, container1,)
        buffer.add(item1, container2)

        with(newEventContext()) {
            on<ItemAddedInContainerEvent> {
                synchronizer.broadcast(containerId)
            }
        }
    }

    @Test
    fun `item creates at cache when copy`(): Unit = with(registry) {
        val prototype = mockk<ContainerEntryPrototype>()
        val entry = testItem(0.0, 0.0)
        every { prototype.copy(entry) } answers { testItem(0.0, 0.0) }
        buffer.copy(entry, cid("A"), prototype)
        buffer.copy(entry, cid("A"), prototype)
        val cache = ItemCache(mockk(relaxed = true))

        with(newEventContext()) {
            buffer.process(cache)
        }
        cache.get(cid("A")) shouldHaveSize 2
    }

    @Test
    fun `EntryMove to Destination_Container переносит предмет между контейнерами и вызывает add+remove`() = with(registry) {
        val from = cid("A")
        val to   = cid("B")

        val entry = testItem(mass = 2.0, volume = 1.0)

        val results = mutableListOf<TransferResult>()
        val requests = qOf(
            EntryMove(entry, Destination.Container(to), MoveMode.Transfer) { results += it }
        )

        val capacities = mapOf(
            to to Capacity.Infinity,
            from to Capacity.Infinity
        )

        val containerItems = mapOf<ContainerEntry, ContainerId>(
            entry to from
        )

        system.update(requests, capacities, containerItems)

        results shouldBe listOf(TransferResult.Success)
        verify(exactly = 1) { buffer.add(entry, to) }
        verify(exactly = 1) { buffer.remove(entry, from) }
        confirmVerified(buffer)
    }

    @Test
    fun `Заполненность (initial Occupancy) учитывает уже лежащие предметы и может заблокировать приём`() = with(registry) {
        val container = cid("C")

        val e1 = testItem(mass = 2.0, volume = 1.0)
        val e2 = testItem(mass = 4.0, volume = 1.0)
        val newEntry = testItem(mass = 1.0, volume = 0.1)

        val results = mutableListOf<TransferResult>()
        val requests = qOf(
            EntryMove(newEntry, Destination.Container(container), MoveMode.Transfer) { results += it }
        )

        val capacities = mapOf(
            container to Capacity.FixedMass(mass = 6.5f)
        )

        val containerItems = mapOf<ContainerEntry, ContainerId>(
            e1 to container,
            e2 to container,
        )

        system.update(requests, capacities, containerItems)

        results shouldBe listOf(TransferResult.Failure)
        verify(exactly = 0) { buffer.add(any(), any()) }
        verify(exactly = 0) { buffer.remove(any(), any()) }
        confirmVerified(buffer)
    }

    @Test
    fun `При MoveMode_COPY создаётся копия и исходник не удаляется, заполненность приёмника растёт`() = with(registry) {
        val from = cid("SRC")
        val to   = cid("DST")
        val prototype = mockk<ContainerEntryPrototype>(relaxed = true)

        val entry = testItem(mass = 2.0, volume = 1.0)

        // Ограничим приёмник по массе ровно одной копией
        val capacities = mapOf(
            to to Capacity.FixedMass(mass = 2.0f),
            from to Capacity.Infinity
        )

        val containerItems = mapOf<ContainerEntry, ContainerId>(
            entry to from
        )

        val results = mutableListOf<TransferResult>()
        val requests = qOf(
            // Первая копия — должна поместиться
            EntryMove(entry, Destination.Container(to), MoveMode.Copy(prototype)) { results += it },
            // Вторая копия — должна не поместиться (заполненность to выросла после первой)
            EntryMove(entry, Destination.Container(to), MoveMode.Copy(prototype)) { results += it }
        )

        system.update(requests, capacities, containerItems)

        results shouldBe listOf(TransferResult.Success, TransferResult.Failure)

        // Было именно copy, а не add/remove
        verify(exactly = 1) { buffer.copy(entry, to, prototype) }
        verify(exactly = 0) { buffer.remove(entry, from) }
        // Во второй раз add/copy не прошли из-за переполнения
        verify(exactly = 0) { buffer.add(any(), any()) }

        confirmVerified(buffer)
    }
}