package org.qbrp.client.render.ui.components.layout

import org.qbrp.client.core.mouse.MouseState
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.ui.components.transform.Pivot
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.require

interface LayoutGroup {
    fun measure(children: List<View>, innerC: Constraints): Size
    fun arrange(children: List<View>, innerRect: Rect)
}

interface LayoutElement {
    fun measure(c: Constraints): Size
}

interface SelfArrange {
    fun arrangeSelf(slot: Rect, transform: RectTransform): Rect
}

enum class SizingPolicy { WRAP, MATCH_PARENT }

data class Sizing(
    val sizingWidth: SizingPolicy = SizingPolicy.WRAP,
    val sizingHeight: SizingPolicy = SizingPolicy.WRAP
)

data class Size(val w: Float = 0f, val h: Float = 0f) {
    constructor(w: Double, h: Double): this(w.toFloat(), h.toFloat())
}

data class Rect(val x: Float, val y: Float, val w: Float, val h: Float) {
    fun deflate(p: Edges) = Rect(
        x + p.l, y + p.t,
        (w - p.horizontal).coerceAtLeast(0f),
        (h - p.vertical).coerceAtLeast(0f)
    )

    fun isIntersect(other: Rect): Boolean =
        x < other.x + other.w && x + w > other.x && y < other.y + other.h && y + h > other.y

}

data class Anchors(
    val minX: Float = 0f,
    val minY: Float = 0f,
    val maxX: Float = 0f,
    val maxY: Float = 0f,
    val pivot: Pivot = Pivot()
) {
    companion object {
        fun fixedPos(x: Float, y: Float, pivot: Pivot) =
            Anchors(x, y, x, y, pivot)
        fun topLeft(x: Float = 0.0f, y: Float = 0.0f) = Anchors(x, y, x, y, Pivot())
        fun cursor(mouseState: MouseState, pivot: Pivot = Pivot()) = fixedPos(mouseState.posNormalized.x, mouseState.posNormalized.y, pivot)
    }
}

data class Edges(val l: Float = 0f, val t: Float = 0f, val r: Float = 0f, val b: Float = 0f) {
    val horizontal get() = l + r
    val vertical   get() = t + b
}

data class Constraints(
    val minW: Float = 0f, val minH: Float = 0f,
    val maxW: Float = Float.POSITIVE_INFINITY,
    val maxH: Float = Float.POSITIVE_INFINITY
) {
    fun loosen() = Constraints(0f, 0f, maxW, maxH)
    fun tightenW(w: Float) = Constraints(w, minH, w, maxH)      // зафиксировать ширину
    fun tightenH(h: Float) = Constraints(minW, h, maxW, h)      // зафиксировать высоту
}

class FixedSize(ctx: ViewContext, val size: Size): ViewComponent(ctx), LayoutElement {
    override fun measure(c: Constraints): Size {
        return Size(size.w.coerceIn(c.minW, c.maxW), size.h.coerceIn(c.minH, c.maxH))
    }
}

fun View.normalizedPointInParent(pointInParent: Vec2): Vec2 {
    val rt = require<RectTransform>()
    val slot = rt.lastParentSlot

    val nx = ((pointInParent.x - slot.x) / slot.w).coerceIn(0f, 1f)
    val ny = ((pointInParent.y - slot.y) / slot.h).coerceIn(0f, 1f)

    return vec2(nx, ny)
}

fun View.normalizedPosOf(pos: Vec2): Vec2 {
    return vec2((pos.x / width), (pos.y / height))
}

fun RectTransform.snapToParentPoint(
    parentSlot: Rect,
    pointInParent: Vec2,
    fixX: Boolean = true,
    fixY: Boolean = true,
    clearMargin: Boolean = true
) {
    val ax = ((pointInParent.x - parentSlot.x) / parentSlot.w).coerceIn(0f, 1f)
    val ay = ((pointInParent.y - parentSlot.y) / parentSlot.h).coerceIn(0f, 1f)

    anchors = anchors.copy(
        minX = if (fixX) ax else anchors.minX,
        maxX = if (fixX) ax else anchors.maxX,
        minY = if (fixY) ay else anchors.minY,
        maxY = if (fixY) ay else anchors.maxY
    )
    if (clearMargin) margin = Edges()   // чтобы позицию не тянул старый внешний отступ
    invalidateArrange()
}

fun RectTransform.fixAnchorsAtParentPivot(
    absPivotX: Float,
    absPivotY: Float,
    clearMargin: Boolean = true
) {
    val p = lastParentSlot
    if (p.w == 0f || p.h == 0f) return

    val ax = (absPivotX - p.x) / p.w
    val ay = (absPivotY - p.y) / p.h

    anchors = Anchors.fixedPos(
        ax.coerceIn(0f, 1f),
        ay.coerceIn(0f, 1f),
        anchors.pivot,
    )
    if (clearMargin) margin = Edges()
}