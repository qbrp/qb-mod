package client.interactions

import core.TestEnv
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import net.minecraft.client.font.TextRenderer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.main.engine.tooltip.TooltipColorProfile
import org.qbrp.client.render.ui.UiComponentRegistrar
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.interaction.list.InteractionListFactory
import org.qbrp.client.render.ui.interaction.panel.distinctObjects
import org.qbrp.client.render.ui.view.Screen
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.engine.controls.entity.display.IconTextureId
import org.qbrp.main.engine.controls.entity.display.old.InteractActivationViewData
import org.qbrp.main.engine.controls.entity.display.InteractionIcon
import org.qbrp.main.engine.controls.entity.display.InteractableName
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.display.InteractionName
import org.qbrp.main.engine.controls.entity.display.old.InteractionViewData
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.text.components.LiteralTextComponent

class InteractionsPanelFactoryTest: TestEnv() {
    private lateinit var registry: ComponentRegistry
    private lateinit var ui: UiContext

    @Test
    fun `interactable view should be equals with similar interactable view`() {
        val interactable1 = InteractableViewData(
            InteractableName(LiteralTextComponent("Sub")),
            InteractionIcon.Asset(IconTextureId("123")),
            -1)
        val interactable2 = InteractableViewData(
            InteractableName(LiteralTextComponent("Sub")),
            InteractionIcon.Asset(IconTextureId("123")),
            -1)
        assert(interactable1 == interactable2)
    }

    @BeforeEach
    fun setup() {
        registry = setupComponentsRegistry { UiComponentRegistrar(this).registerAll() }
        ui = UiContext(mockk(relaxed = true), registry, mockk(relaxed = true))
    }

    @Test
    fun `fabric creates interactions list and request interaction icons for object and subject`(): Unit = with(ui) {
        val screenMock = mockk<Screen>()
        every { screenMock.width } returns 200f
        every { screenMock.height } returns 200f
        val iconViewFactory = spyk(IconViewFactory(mockk(relaxed = true), mockk(relaxed = true)))

        // Мок activation и его возвращаемые данные
        val objectIcon = InteractionIcon.Asset(IconTextureId("123"))
        val objectView = InteractableViewData(
            InteractableName(LiteralTextComponent("Obj")),
            objectIcon,
            -1)
        val subjectIcon = InteractionIcon.Asset(IconTextureId("123"))
        val activation = InteractActivationViewData(listOf(
            InteractionViewData(
                InteractableViewData(
                    InteractableName(LiteralTextComponent("Sub")),
                    subjectIcon,
                    -1),
                objectView,
                InteractionName(LiteralTextComponent("Взаимодействие")),
                Intent.DEFAULT,
                true,
                listOf(),
                listOf()
            )
        ))

        activation.distinctObjects().size shouldBe 1
        val textRenderer = mockk<TextRenderer>(relaxed = true)

        val factory = InteractionListFactory(
            mockk(relaxed = true),
            mockk(relaxed = true),
            iconViewFactory,
            mockk(relaxed = true),
            TooltipColorProfile.GRAY,
            mockk(relaxed = true)
        )
        with(textRenderer) {
            val panel = factory.create(activation = activation)

            verify { iconViewFactory.create(objectIcon, any()) }
            verify { iconViewFactory.create(subjectIcon, any()) }
            panel.view.children.shouldHaveSize(2)
        }
    }
}
