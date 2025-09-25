package org.qbrp.client.render.ui.components.transform

import net.minecraft.client.MinecraftClient
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.Rect
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.client.render.window.Window
import org.qbrp.main.core.components.extensions.get

class RootLayout(ctx: ViewContext) : ViewComponent(ctx) {
    var inLayout: Boolean = false
        private set
    private var dirtyMeasure: Boolean = false
    private var dirtyArrange: Boolean = false

    private fun performFullLayout() {
        if (inLayout) return
        inLayout = true
        try {
            val rt = view.get<RectTransform>() ?: return

            rt.onMeasure(view.constraints)
            rt.onArrange(view.rect, false)

            val allTransforms: Map<View, RectTransform> = view.children
                .mapNotNull { child -> child.get<RectTransform>()?.let { t -> child to t } }
                .toMap()

            val childWithOverlapTransforms: Map<View, RectTransform> =
                allTransforms.filterValues { it.hitTest }

            childWithOverlapTransforms.forEach { (child, transform) ->
                val anchors0 = transform.anchors
                var minX = anchors0.minX
                var minY = anchors0.minY
                var maxX = anchors0.maxX
                var maxY = anchors0.maxY

                val obstacles = childWithOverlapTransforms.toMutableMap().apply { remove(child) }

                while (obstacles.any { (_, other) ->
                        transform.lastSelfRect.isIntersect(other.lastSelfRect)
                    }) {

                    minY += OVERLAP_TEST_STEP
                    maxY += OVERLAP_TEST_STEP

                    if (maxY > 1f) {
                        minY = anchors0.minY
                        maxY = anchors0.maxY
                        minX += OVERLAP_TEST_STEP
                        maxX += OVERLAP_TEST_STEP
                    }
                    if (maxX > 1f) break

                    val w = maxX - minX
                    val h = maxY - minY
                    minX = minX.coerceIn(0f, 1f - w)
                    minY = minY.coerceIn(0f, 1f - h)
                    maxX = minX + w
                    maxY = minY + h

                    transform.anchors = anchors0.copy(
                        minX = minX, minY = minY, maxX = maxX, maxY = maxY
                    )
                    rt.onArrange(view.rect, false)
                }
            }

            allTransforms.values.forEach { t ->
                val a = t.anchors
                val rect = t.lastSelfRect
                val w = (a.maxX - a.minX).coerceIn(0f, 1f)
                val h = (a.maxY - a.minY).coerceIn(0f, 1f)

                val minX = a.minX.coerceIn(0f, 1f - w)
                val minY = a.minY.coerceIn(0f, 1f - h)
                val maxX = minX + w
                val maxY = minY + h


                t.anchors = a.copy(minX = minX, minY = minY, maxX = maxX, maxY = maxY)
            }

            rt.onArrange(view.rect, false)
        } finally {
            inLayout = false
        }
    }


    private fun performArrangeOnly() {
        if (inLayout) return
        inLayout = true
        try {
            val rt = view.get<RectTransform>() ?: return
            val w = view.width
            val h = view.height
            rt.onArrange(Rect(0f, 0f, w, h), false)
        } finally {
            inLayout = false
        }
    }

    private fun applyInvalidations() {
        when {
            dirtyMeasure -> {
                dirtyMeasure = false
                dirtyArrange = false // arrange выполнится в любом случае
                performFullLayout()
            }
            dirtyArrange -> {
                dirtyArrange = false
                performArrangeOnly()
            }
        }
    }
    fun invalidateMeasure() {
        dirtyMeasure = true
    }

    fun invalidateArrange() {
        if (!dirtyMeasure) dirtyArrange = true
    }

    override fun onAddView(view: View) {
        invalidateMeasure()
    }

    override fun onInit() {
        performFullLayout()
    }

    override fun onResize(client: MinecraftClient, window: Window) {
        performFullLayout()
    }

    override fun onTransform(ctx: Draw2dContext) {
        applyInvalidations()
    }

    companion object {
        const val OVERLAP_TEST_STEP = 0.03f
        val View.rect get() = Rect(0f, 0f, width, height)
        val View.constraints get() = Constraints(0f, 0f, width, height)
    }
}
