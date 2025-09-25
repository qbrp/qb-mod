package org.qbrp.client.render.ui.dsl

import org.qbrp.client.render.Render
import org.qbrp.client.render.animation.Easing
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.CloseWidget
import org.qbrp.client.render.ui.components.behaviour.OnClick
import org.qbrp.client.render.ui.components.layout.Anchors
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.Edges
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.transform.Pivot
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.View
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.Colors

context(ctx: UiContext)
fun widget(id: String, buildView: WidgetScope.() -> Unit): View {
    val node = WidgetScope(id).apply { buildView() }.build()
    return WidgetBuildSystem().build(node)
}

@UiDsl
class WidgetScope(private val id: String) {
    private var titleText: String? = null
    private var contentBuild: ViewBuildContinue? = null
    private var anchors: Anchors = Anchors()
    private var actionBarExtension: ActionBarExtension? = null
    private var root: ViewBuildContinue = {}

    fun title(text: String) {
        titleText = text
    }

    fun root(build: ViewBuildContinue) {
        root = build
    }

    fun actionbar(buildView: ActionBarScope.() -> Unit) {
        actionBarExtension = ActionBarScope().apply { buildView() }.build()
    }

    fun content(buildContinue: ViewBuildContinue) {
        contentBuild = buildContinue
    }

    fun anchors(left: Float, top: Float, right: Float, bottom: Float, pivot: Pivot = Pivot()) {
        this.anchors = Anchors(left, top, right, bottom, pivot)
    }

    fun anchors(x: Float, y: Float, pivot: Pivot = Pivot()) {
        this.anchors = Anchors(x, y, x, y, pivot)
    }

    fun build(): WidgetNode {
        val contentBuild = contentBuild!!
        return WidgetNode(id, titleText, contentBuild, root, anchors, actionBarExtension)
    }
}

@UiDsl
class ActionBarScope {
    private var elems: MutableList<ActionBarElement> = mutableListOf()

    fun button(width: Float = 8f, height: Float = 8f, buildContinue: ViewBuildContinue) {
        elems.add(ActionBarElement.Button(Size(width, height), buildContinue))
    }

    fun build() = ActionBarExtension(elems)
}

sealed class ActionBarElement {
    data class Button(val size: Size, val build: ViewBuildContinue): ActionBarElement()
}

data class ActionBarExtension(val elements: List<ActionBarElement>)

data class WidgetNode(
    val id: String,
    val title: String?,
    val content: ViewBuildContinue,
    val root: ViewBuildContinue,
    val anchors: Anchors,
    val actionBarExtension: ActionBarExtension?
)

class WidgetBuildSystem() {
    internal enum class ActionBarPosition { HORIZONTAL, VERTICAL }

    context(ctx: UiContext)
    fun build(n: WidgetNode): View {
        val content = content(n)
        val contentSize = content.require<RectTransform>().onMeasure(Constraints())
        val drag = drag()

        val extensionElements = n.actionBarExtension?.elements?.map {
            viewOf {
                when (it) {
                    is ActionBarElement.Button -> {
                        background(Colors.BACKGROUND)
                        fixedSize(it.size.w, it.size.h)
                        use(it.build)
                    }
                }
            }
        } ?: listOf()

        val actionBar = actionBar(drag, close(n.id), extensionElements, ActionBarPosition.VERTICAL)
        val actionBarSize = actionBar.require<RectTransform>().onMeasure(Constraints())
        val actionBarPosition = if (actionBarSize.h <= contentSize.h) ActionBarPosition.VERTICAL else ActionBarPosition.HORIZONTAL

        val useActionBar = when (actionBarPosition) {
            ActionBarPosition.HORIZONTAL -> actionBar(drag, close(n.id), extensionElements, ActionBarPosition.HORIZONTAL)
            ActionBarPosition.VERTICAL -> actionBar
        }
        val views = when(actionBarPosition) {
            ActionBarPosition.HORIZONTAL -> listOf(useActionBar, content)
            ActionBarPosition.VERTICAL -> listOf(useActionBar, content)
        }

        return buildView {
            anchors(n.anchors.minX, n.anchors.minY, n.anchors.maxX, n.anchors.maxY, n.anchors.pivot)
            use(n.root)

            when (actionBarPosition) {
               ActionBarPosition.HORIZONTAL -> vertical(2f)
               ActionBarPosition.VERTICAL -> horizontal(2f)
            }
            views.forEach { view(it) }

            draggableBy(drag)
        }.finish()
    }

    context(ctx: UiContext)
    private fun content(n: WidgetNode) = buildView {
        vertical(2f)
        padding(2f, 2f, 2f, 2f)
        background(Colors.BACKGROUND)
        n.title?.let { view(title(it)) }

        view {
            use(n.content)
        }
    }.finish()

    context(ctx: UiContext)
    private fun actionBar(
        drag: View,
        close: View,
        elements: List<View>,
        position: ActionBarPosition
    ) =
        buildView {
            when (position) {
                ActionBarPosition.HORIZONTAL -> horizontal(2f)
                ActionBarPosition.VERTICAL -> vertical(2f)
            }
            view(drag)
            view(close)
            views(elements)

            hover {
                gap(20f)
                translucent()
            }

//            animate {
//                animation {
//                    hoverSlideUp(blockInput = listOf(close) + elements, draggableRef = { drag }) {
//                        appear { v, a ->
//                            a.from = if (a.value == 0f) 1f else a.value; a.to = 0f
//                        }
//                        disappear { _, a ->
//                            a.from = a.value; a.to = 1f
//                        }
//                        scissor = { v -> Edges(0f, 0f, v.width, v.height) }
//                    }
//
//                    duration(150f)
//                    once()
//
//                    easing { EASE_OUT_QUAD }
//                }
//            }
        }.finish()

    context(ctx: UiContext)
    private fun title(text: String) = buildView {
        text(text)
    }.finish()

    context(ctx: UiContext)
    private fun drag() = buildView {
        background(Colors.BACKGROUND)
        fixedSize(8f, 8f)
        texture(Render.DRAG_N_DROP, 16f)
    }.finish()

    context(ctx: UiContext)
    private fun close(id: String) = buildView {
        background(Colors.BACKGROUND)
        component { OnClick(it, CloseWidget(id)) }
        fixedSize(8f, 8f)
        texture(Render.DOT, 16f)
    }.finish()
}