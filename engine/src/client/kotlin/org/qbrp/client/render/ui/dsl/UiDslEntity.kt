package org.qbrp.client.render.ui.dsl

import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.ZIndex
import org.qbrp.client.render.ui.components.behaviour.HighlightParams
import org.qbrp.client.render.ui.components.layout.Anchors
import org.qbrp.client.render.ui.components.layout.Edges
import org.qbrp.client.render.ui.components.layout.Sizing
import org.qbrp.client.render.ui.components.layout.linear.VerticalDirection
import org.qbrp.client.render.ui.components.text.TextSupplier
import org.qbrp.client.render.ui.view.HitBehavior
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.engine.text.components.TextComponent

@DslMarker annotation class UiDsl

sealed interface Node
data class ViewNode(
    val layout: Layout? = null,
    val components: List<Component> = emptyList(),
    val children: List<Node> = emptyList(),
    val minWidth: Float,
    val minHeight: Float,
    val maxWidth: Float,
    val maxHeight: Float,
    val customView: View?,
    val anchors: Anchors,
    var margin: Edges,
    var padding: Edges,
    val fixedPos: Vec2?,
    val sizing: Sizing?,
    val zIndex: ZIndex,
    val allowAnchors: Boolean,
    val overlap: Boolean,
    val topmost: Boolean,
    val postCreate: ViewPostCreate,
    val reference: ViewRef,
    val visible: Boolean,
    val hoverGap: Float,
    val hoverHitTest: HitBehavior
) : Node

data class CustomView(val view: View): Node

sealed interface Layout
data class VerticalNode(val gap: Float, val direction: VerticalDirection) : Layout
data class HorizontalNode(val gap: Float) : Layout
data class AutoGridNode(val cols: Int,
                        val colsGap: Float,
                        val rowGap: Float,
                        val colMin: FloatArray?,
                        val cells: List<CellNode>) : Layout {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AutoGridNode

        if (cols != other.cols) return false
        if (colsGap != other.colsGap) return false
        if (rowGap != other.rowGap) return false
        if (!colMin.contentEquals(other.colMin)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cols
        result = 31 * result + colsGap.hashCode()
        result = 31 * result + rowGap.hashCode()
        result = 31 * result + (colMin?.contentHashCode() ?: 0)
        return result
    }
}

data class CellNode(val r: Int, val c: Int, val view: ViewNode)

sealed interface Component
data class BackgroundNode(val color: Int, val padding: Float) : Component
data class TextNode(val value: TextSupplier) : Component
data class Custom(val builder: (ViewContext) -> ViewComponent) : Component
data class DraggableBy(val view: View): Component