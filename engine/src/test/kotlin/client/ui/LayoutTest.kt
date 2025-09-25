package client.ui

import core.TestEnv
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.client.render.ui.UiComponentRegistrar
import org.qbrp.client.render.ui.components.layout.Anchors
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.FixedSize
import org.qbrp.client.render.ui.components.layout.LayoutComponent
import org.qbrp.client.render.ui.components.layout.Rect

import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.layout.Sizing
import org.qbrp.client.render.ui.components.layout.SizingPolicy
import org.qbrp.client.render.ui.components.layout.linear.VerticalLayout
import org.qbrp.client.render.ui.components.transform.Pivot
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.components.transform.RootLayout
import org.qbrp.client.render.ui.view.View
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.remove
import org.qbrp.main.core.components.extensions.require

class LayoutTest: TestEnv() {
    private lateinit var registry: ComponentRegistry

    @BeforeEach
    fun setup() {
        registry = setupComponentsRegistry { UiComponentRegistrar(this).registerAll() }
    }

    @Test
    fun `root view handler relayout after exception catched`(): Unit = with(registry) {
        val screen = View()
        val exceptionRectTransform = mockk<RectTransform>()
        every { exceptionRectTransform.onMeasure(any()) } throws RuntimeException()
        screen.add(exceptionRectTransform)
        val transform = spyk(RootLayout(screen.componentContext))
        screen.add(transform)

        shouldThrow<RuntimeException> {
            transform.invalidateMeasure()
            transform.onTransform(mockk())
        }

        screen.remove<RectTransform>()
        screen.add(RectTransform(screen.componentContext)) //рабочий rectTransform
        shouldNotThrow<RuntimeException> {
            transform.invalidateMeasure()
            transform.onTransform(mockk())
            assert(!transform.inLayout)
        }
    }

    @Test
    fun `root view handler relayout transforms when child added`(): Unit = with(registry) {
        val screen = View()
        val rect = spyk(RectTransform(screen.componentContext))
        screen.add(rect)
        val transform = spyk(RootLayout(screen.componentContext))
        screen.add(transform)
        screen.addView(View().apply { add(RectTransform(componentContext)) })

        transform.onTransform(mockk())

        verify { transform.invalidateMeasure() }
        verify { rect.onMeasure(any()) }
        verify { rect.onArrange(any(), false) }
    }

    @Test
    fun `vertical layout positioning views correctly`() = with(registry) {
        val W = 800f
        val H = 600f

        val panel = View().apply {
            add(
                RectTransform(
                    componentContext,
                    anchors = Anchors.topLeft(),
                    sizing = Sizing(sizingHeight = SizingPolicy.WRAP)
                )
            )
            add(LayoutComponent(componentContext, VerticalLayout(0f)))// вертикальный лэйаут

            addView(View().apply {
                add(RectTransform(componentContext))
                add(FixedSize(componentContext, Size(100f, 100f)))
            })
            addView(View().apply {
                add(RectTransform(componentContext, sizing = Sizing(sizingHeight = SizingPolicy.MATCH_PARENT)))
                add(FixedSize(componentContext, Size(100f, 100f)))
            })
        }

        // Один проход лэйаута
        panel.require<RectTransform>().onMeasure(Constraints(0f, 0f, W, H))
        panel.require<RectTransform>().onArrange(Rect(0f, 0f, W, H), true)

        // Проверка
        println("panel: x=${panel.x}, y=${panel.y}, w=${panel.width}, h=${panel.height}")
        panel.width shouldBe 100f
        panel.height shouldBe 200f
        panel.children.forEachIndexed { i, v ->
            v.width shouldBe 100f
            println("child $i: x=${v.x}, y=${v.y}, w=${v.width}, h=${v.height}")
        }
    }

    @Test
    fun `centered anchors should put view in center`(): Unit = with(registry) {
        val W = 100f
        val H = 100f

        val dot = View().apply {
            add(RectTransform(componentContext, anchors = Anchors.fixedPos(0.5f, 0.5f, Pivot(0.5f, 0.5f)), minH = 50f, minW = 50f))
        }
        dot.require<RectTransform>().onMeasure(Constraints(0f, 0f, W, H))
        dot.require<RectTransform>().onArrange(Rect(0f, 0f, W, H), true)

        dot.width shouldBe 50f
        dot.height shouldBe 50f
        dot.x shouldBe 25f
        dot.y shouldBe 25f
    }
}