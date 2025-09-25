package core.events

import core.TestEnv
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.events.EventSupportersStorage
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.events.DefaultEventContext
import org.qbrp.main.core.events.EventId
import org.qbrp.main.core.events.component.ComponentEventContext
import org.qbrp.main.core.events.emit
import org.qbrp.main.core.events.on
import org.qbrp.main.core.events.onComponentEvent
import org.qbrp.main.core.game.storage.GlobalStorage

class EventTests: TestEnv() {
    private lateinit var eventContext: ComponentEventContext

    @BeforeEach
    fun setup() {
        eventContext = newEventContext()
    }

    @Test
    fun listEventCollectorsWorks(): Unit = with(eventContext) {
        val list = listOf("")
        val list2 = listOf("2")
        on<SignalEvent, List<String>> { list }
        on<SignalEvent, List<String>> { list2 }
        shouldNotThrow<Exception> {
            SignalEvent().collect<List<String>>().flatMap { it }.first()
        }
    }

    @Test
    fun transitiveEventsWorks() = with(eventContext) {
        var signal1 = false
        var signal2 = false
        val signal1Event = EventId("signal1")
        val signal2Event = EventId("signal2")
        on<SignalEvent>(signal1Event) { SignalEvent().emit(signal2Event) }
        on<SignalEvent>(signal2Event) { signal1 = true }
        on<SignalEvent>(signal2Event) { signal2 = true }
        SignalEvent().emit(signal1Event)
        assert(signal1)
        assert(signal2)
    }

    @Test
    fun eventDontHandleIfCancelled() = with(eventContext) {
        var invokedFirst = false
        var invokedSecond = false
        on<TestCancellableEvent> { invokedFirst = true; it.cancel() }
        on<TestCancellableEvent> { invokedSecond = true }
        TestCancellableEvent().emit()
        assert(invokedFirst)
        assert(!invokedSecond)
    }

    @Test
    fun eventCallsListeners() {
        val event = TestEvent("Test chatText", 10)
        var handled = false
        with(eventContext) {
            on<TestEvent> { handled = true }
            event.emit()
        }
        assert(handled) { "Событие не было обработано" }
    }

    @Test
    fun eventWithHighPriorityExecutesEarlierThanLowPriority() {
        val calls = mutableListOf<Int>()

        with(eventContext) {
            on<SignalEvent>(0) { calls += 0 }
            on<SignalEvent>(1) { calls += 1 }

            SignalEvent().emit()
        }

        calls shouldBe listOf(1, 0)
    }


    @Test
    fun componentEventsPropagatesAllEntities() {
        val component = TestComponent(false)
        val entity = TestStateEntity().apply { addComponent(component, name = "TestComponent") }
        val storage1 = GlobalStorage<StateObject<out ComponentManager>>().apply { add(entity) }
        val storage = EventSupportersStorage().apply { addStorage(storage1) }
        with(DefaultEventContext(newEventBus(), storage)) {
            onComponentEvent<SignalEvent, TestComponent> { event, component ->
                component.invoke()
            }
            SignalEvent().emit()
        }
        assert(component.invoked) { "Событие не было вызвано" }
    }

    @Test
    fun singleEventWithMultipleCollectorsNotThrowsCastException() = with(eventContext) {
        shouldNotThrow<ClassCastException> {
            on<SignalEvent, String> { "String" }
            on<SignalEvent, Boolean> { true }
            SignalEvent().collect<String>()
            SignalEvent().collect<Boolean>()
        }
    }
}