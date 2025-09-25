package org.qbrp.client.render.ui.dsl

import net.minecraft.client.font.TextRenderer
import org.koin.core.qualifier.named
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.behaviour.Draggable
import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.components.behaviour.OnHover
import org.qbrp.client.render.ui.components.layout.Anchors
import org.qbrp.client.render.ui.components.layout.LayoutComponent
import org.qbrp.client.render.ui.components.layout.Sizing
import org.qbrp.client.render.ui.components.layout.grid.AutoGridLayout
import org.qbrp.client.render.ui.components.layout.grid.GridPosition
import org.qbrp.client.render.ui.components.layout.grid.GridProportions
import org.qbrp.client.render.ui.components.layout.grid.HighlightGridRow
import org.qbrp.client.render.ui.components.layout.linear.HorizontalLayout
import org.qbrp.client.render.ui.components.layout.linear.VerticalLayout
import org.qbrp.client.render.ui.components.text.Text
import org.qbrp.client.render.ui.components.texture.Background
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.Screen
import org.qbrp.client.render.ui.view.View
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.has
import org.qbrp.main.core.components.extensions.require

class ViewRef() {
    private var view: View? = null

    fun get(): View {
        return view ?: error("View не был инициализирован")
    }

    fun set(v: View) {
        view = v
    }
}

context(ui: UiContext)
fun ViewNode.finish(): View {
    val postCreate = mutableListOf<Runnable>()
    val node = this
    val v = with(postCreate) { BuildSystem(ui.componentRegistry).build(node) }
    postCreate.forEach { it.run() }
    return v
}

class BuildSystem(val registry: ComponentRegistry) {
    context(ui: UiContext, postCreationList: MutableList<Runnable>)
    fun build(node: Node): View {
        if (node is CustomView) return node.view

        require(node is ViewNode)

        val v = node.customView ?: View(zIndex = node.zIndex)
        v.topmost = node.topmost
        v.visible = node.visible
        v.hoverGap = node.hoverGap
        v.hitBehavior = node.hoverHitTest

        val placement = if (node.fixedPos != null) Anchors.fixedPos(
            node.fixedPos.x,
            node.fixedPos.y,
            node.anchors.pivot
        ) else node.anchors
        val transform = RectTransform(
            v.componentContext,
            placement,
            node.sizing ?: Sizing(),
            node.padding,
            node.margin,
            node.minWidth,
            node.minHeight,
            node.maxWidth,
            node.maxHeight,
            node.overlap,
            node.allowAnchors
        )
        v.add(transform)

        node.components.filterIsInstance<DraggableBy>().firstOrNull()?.let {
            it.view.add(Draggable(it.view.componentContext, v))
        }

        when (val l = node.layout) {
            is VerticalNode -> {
                v.add(LayoutComponent(v.componentContext, VerticalLayout(l.gap, l.direction)))
                node.children.forEach { child -> v.addView(build(child)) }
                node.components.forEach { addComponent(v, node, it) }
            }

            is HorizontalNode -> {
                v.add(LayoutComponent(v.componentContext, HorizontalLayout(l.gap)))
                node.children.forEach { child -> v.addView(build(child)) }
                node.components.forEach { addComponent(v, node,it) }
            }

            is AutoGridNode -> {
                val grid = AutoGridLayout(
                    v.componentContext,
                    l.cols,
                    l.colsGap,
                    l.rowGap,
                    l.colMin
                )
                v.add(grid)

                l.cells.forEach { cell ->
                    val childView = build(cell.view)
                    childView.add(GridPosition(childView.componentContext, cell.c, cell.r))
                    v.addView(childView)
                }
                node.components.forEach { addComponent(v, node,it) }

            }

            null -> {
                node.components.forEach { addComponent(v, node,it) }
                node.children.forEach { child -> v.addView(build(child)) }
            }
        }

        node.reference.set(v)
        postCreationList += Runnable { node.postCreate(v) }

        return v
    }

    context(ui: UiContext)
    private fun addComponent(target: View, node: ViewNode, c: Component) {
        val component = when (c) {
            is BackgroundNode -> {
                Background(target.componentContext, c.color, c.padding)
            }

            is TextNode -> {
                Text(target.componentContext, c.value, textRenderer = ui.textRenderer)
            }

            is Custom -> {
                c.builder(target.componentContext)
            }

            is DraggableBy -> { null }
        }
        if (component == null) return
        val componentTypeName = registry.getComponentName(component)
        val componentClass = component::class
        val name = if (target.has(componentClass)) {
            val count = target.count { it::class == componentClass}
            "${componentTypeName}#$count"
        } else {
            componentTypeName
        }
        target.addComponent(component, name = name)
    }
}