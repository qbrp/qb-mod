package engine.interactions

import core.TestEnv
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import net.minecraft.text.Text
import org.junit.jupiter.api.Test
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableType
import org.qbrp.main.engine.controls.service.activation.InteractionActivation
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactions.Executor
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.network.player.DisconnectReason
import java.util.UUID

class InteractionActivationTest: TestEnv() {
    val typeA = InteractableType<DummyInteractable>("dummyA")
    val typeB = InteractableType<DummyInteractable>("dummyB")
    val typeC = InteractableType<DummyInteractable>("dummyC")
    val dummyA = DummyInteractable("Интерактивный A", typeA)
    val dummyB = DummyInteractable("Интерактивный B", typeB)
    val dummyC = DummyInteractable("Интерактивный C", typeC)

    @Test
    /**
     * Взаимодействие субъекта **A** и объекта **B**. На вход идут объекты и субъекты **А, B**, **С**
     * */
    fun `interactions filters correctly`(): Unit = with(newEventContext()) {
        val dummies = listOf(dummyA, dummyB, dummyC)
        val interaction = DummyInteraction("А + B", typeA, typeB, listOf())
        val lookup = DummyLookup(listOf(interaction), dummies, dummies)
        val context = mockk<InteractionContext>(relaxed = true)
        InteractionActivation(context).apply {
            find(lookup)
            interactions.shouldHaveSize(1)
            assert(hasInteraction(interaction))
        }
    }

    @Test
    fun `non symmetric interaction dont filters`(): Unit = with(newEventContext()) {
        val interactionAB = DummyInteraction("А -> B", typeA, typeB, listOf())
        val lookup = DummyLookup(listOf(interactionAB), listOf(dummyA, dummyB), listOf(dummyA, dummyB))
        val context = mockk<InteractionContext>(relaxed = true)
        InteractionActivation(context).apply {
            find(lookup)
            interactions.shouldHaveSize(1)
        }
    }

    @Test
    fun `empty lookup returns empty lists`() {
        val lookup = DummyLookup(emptyList(), emptyList(), emptyList())

        assert(lookup.getInteractableSubjects().isEmpty())
        assert(lookup.getInteractableObjects().isEmpty())
        assert(lookup.getAnyInteractions().isEmpty())
    }

    @Test
    fun `interaction with false show condition dont filters`(): Unit = with(newEventContext()) {
        val falseCondition = mockk<Condition<Interactable, Interactable>>()
        every { falseCondition.show(any(), any(), any()) } returns false
        val interactionAB = DummyInteraction("А -> B", typeA, typeB, listOf(), conditions = listOf(falseCondition))
        val lookup = DummyLookup(listOf(interactionAB), listOf(dummyA, dummyB), listOf(dummyA, dummyB))
        val context = mockk<InteractionContext>(relaxed = true)
        InteractionActivation(context, false).apply {
            find(lookup)
            interactions.shouldHaveSize(0)
        }
    }

    @Test
    fun `interaction will shown if field snowAll is true in activation`(): Unit = with(newEventContext()) {
        val falseCondition = mockk<Condition<Interactable, Interactable>>()
        every { falseCondition.show(any(), any(), any()) } returns false
        val interactionAB = DummyInteraction("А -> B", typeA, typeB, listOf(), conditions = listOf(falseCondition))
        val lookup = DummyLookup(listOf(interactionAB), listOf(dummyA, dummyB), listOf(dummyA, dummyB))
        val context = mockk<InteractionContext>(relaxed = true)
        InteractionActivation(context, true).apply {
            find(lookup)
            interactions.shouldHaveSize(1)
        }
    }

    @Test
    fun `typed interactions casts safely`(): Unit = with(newMinecraftAndEventContext()) {
        val typedCondition = mockk<Condition<DummyInteractable, DummyInteractable>>()
        every { typedCondition.show(any(), any(), any()) } returns true
        val typedExecutor = mockk<Executor<DummyInteractable, DummyInteractable>>()
        every { typedExecutor.execute(any(), any(), any()) } just Runs
        val interactionAB = TypedDummyInteraction("А -> B", typeA, typeB, listOf(typedExecutor), conditions = listOf(typedCondition))
        val lookup = DummyLookup(listOf(interactionAB), listOf(dummyA, dummyB), listOf(dummyA, dummyB))
        val context = mockk<InteractionContext>(relaxed = true)
        val player = mockk<ServerPlayerObject>(relaxed = true)
        shouldNotThrow<Exception> {
            InteractionActivation(context, true).apply {
                find(lookup)
                interactions.shouldHaveSize(1)
                execute(player)
            }
        }
    }

    @Test
    fun playerDisconnectsIfExceptionThrowsWhileInteractionExecution() = with(newEventContext()) {
        val ctx = newMinecraftContext()
        val interactionAB = DummyInteraction("А -> B", typeA, typeB, listOf(), throwsException = true)
        val lookup = DummyLookup(listOf(interactionAB), listOf(dummyA, dummyB), listOf(dummyA, dummyB))
        val context = mockk<InteractionContext>(relaxed = true)
        val pUuid = MinecraftPlayerId(UUID.randomUUID())
        val player = mockk<ServerPlayerObject>(relaxed = true) {
            every { uuid } returns pUuid
        }
        with(ctx) {
            InteractionActivation(context, true).apply {
                find(lookup)
                execute(player)
            }
            verify { playerModeration.disconnect(pUuid, DisconnectReason(any<Text>())) }
        }
    }
}