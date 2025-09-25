package org.qbrp.client.render.ui.components.transform

import org.qbrp.client.render.Drawable
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.layout.Anchors
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.Edges
import org.qbrp.client.render.ui.components.layout.LayoutElement
import org.qbrp.client.render.ui.components.layout.LayoutGroup
import org.qbrp.client.render.ui.components.layout.Rect
import org.qbrp.client.render.ui.components.layout.SelfArrange
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.layout.Sizing
import org.qbrp.client.render.ui.view.Screen
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require

var View.hitTest
    get() = this.require<RectTransform>().hitTest
    set(value) {
        this.require<RectTransform>().hitTest = value
    }

class RectTransform(
    ctx: ViewContext,
    var anchors: Anchors = Anchors(),
    var sizing: Sizing = Sizing(),
    var padding: Edges = Edges(),
    var margin: Edges = Edges(),
    var minW: Float = 0f,
    var minH: Float = 0f,
    var maxW: Float = Float.POSITIVE_INFINITY,
    var maxH: Float = Float.POSITIVE_INFINITY,
    var hitTest: Boolean = true,
    var useAnchors: Boolean = true
) : ViewComponent(ctx), Drawable {
    var measured = Size()
    var measureDirty = true
    var arrangeDirty = true
    var lastSelfRect: Rect = Rect(0f, 0f, 0f, 0f)
    var lastParentSlot: Rect = Rect(0f, 0f, 0f, 0f)

    override fun onRender(ctx: Draw2dContext) {
        //if (view is Screen) return
        //ctx.drawBorder(0f, 0f, lastSelfRect.w, lastSelfRect.h, 4000f, Colors.GREEN_LIGHT)
    }

    fun invalidateMeasure() {
        if (measureDirty && arrangeDirty) return
        measureDirty = true
        arrangeDirty = true
        var p = view.parent
        while (view !is Screen) {
            p.get<RectTransform>()?.let {
                if (it.measureDirty) break
                it.measureDirty = true
                it.arrangeDirty = true
            }
            p.get<RootLayout>()?.let {
                it.invalidateMeasure()
                return
            }
            p = p.parent
        }
    }

    fun invalidateArrange() {
        if (arrangeDirty) return
        arrangeDirty = true
        var p = view.parent
        while (view !is Screen) {
            p.get<RectTransform>()?.let {
                if (it.arrangeDirty) break
                it.arrangeDirty = true
            }
            p.get<RootLayout>()?.let {
                it.invalidateMeasure()
                return
            }
            p = p.parent
        }
    }


    fun onMeasure(constraints: Constraints): Size {
        val innerMaxW = (constraints.maxW - padding.horizontal).coerceAtLeast(0f)
        val innerMaxH = (constraints.maxH - padding.vertical).coerceAtLeast(0f)
        val innerSpaceConstraints = Constraints(
            0f,
            0f,
            innerMaxW,
            innerMaxH
        ) //Внутреннее пространство виджета, доступное для контента, к которому применен паддинг

        val elem = view.get<LayoutElement>()
        val layout = view.get<LayoutGroup>()
        val contentSize = when {
            elem != null -> elem.measure(innerSpaceConstraints)
            layout != null -> layout.measure(view.children, innerSpaceConstraints)
            else -> {
                var maxW = 0f;
                var maxH = 0f
                for (child in view.children) {
                    val measure = child.get<RectTransform>()?.onMeasure(innerSpaceConstraints) ?: continue
                    maxW = maxOf(maxW, measure.w)
                    maxH = maxOf(maxH, measure.h)
                }
                Size(maxW, maxH)
            }
        }

        //Общий требуемый размер
        measured = Size(
            (contentSize.w + padding.horizontal)
                .coerceIn(constraints.minW, constraints.maxW)
                .coerceIn(minW, maxW),
            (contentSize.h + padding.vertical)
                .coerceIn(constraints.minH, constraints.maxH)
                .coerceIn(minH, maxH)
        )
        measureDirty = false

        return measured
    }

    fun onArrange(slot: Rect, applyAnchors: Boolean) {
        val parentSlot = if (applyAnchors) slot.deflate(margin) else slot
        val self = if (applyAnchors && useAnchors) {
            view.get<SelfArrange>()?.arrangeSelf(parentSlot, this) ?: arrangeSelf(parentSlot)
        } else {
            parentSlot
        }

        lastParentSlot = parentSlot
        lastSelfRect   = self

        view.setPos(self.x, self.y)
        view.width = self.w; view.height = self.h

        val layout = view.get<LayoutGroup>()
        val inner  = self.deflate(padding)

        val childSlot = Rect(padding.l, padding.t, inner.w, inner.h)

        if (layout != null) {
            layout.arrange(view.children, childSlot)
        } else {
            for (child in view.children) {
                child.get<RectTransform>()?.onArrange(childSlot, applyAnchors = true)
            }
        }
        arrangeDirty = false
    }

    private fun arrangeSelf(slot: Rect): Rect {
        fun axis(min: Float, max: Float, pivot: Float,
                 measured: Float, minV: Float, maxV: Float, extent: Float): Pair<Float, Float> {
            return if (min == max) {
                val v = measured.coerceIn(minV, maxV)
                val pos = extent * min - pivot * v                 // фикс-якорь
                pos to v
            } else {
                val base = (extent * (max - min)).coerceAtLeast(0f).coerceIn(minV, maxV) // стретч
                (extent * min) to base
            }
        }
        fun clampInside0(pos: Float, size: Float, extent: Float): Float {
            if (size >= extent) return 0f
            return pos.coerceIn(0f, extent - size)
        }

        val (x0, w) = axis(anchors.minX, anchors.maxX, anchors.pivot.x, measured.w, minW, maxW, slot.w)
        val (y0, h) = axis(anchors.minY, anchors.maxY, anchors.pivot.y, measured.h, minH, maxH, slot.h)

        val x = clampInside0(x0, w, slot.w)
        val y = clampInside0(y0, h, slot.h)

        return Rect(
            x,
            y,
            w,
            h
        )
    }
}