package org.qbrp.client.render.ui.components.layout.linear

import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.LayoutGroup
import org.qbrp.client.render.ui.components.layout.Rect
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.layout.Sizing
import org.qbrp.client.render.ui.components.layout.SizingPolicy
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.View
import org.qbrp.main.core.components.extensions.require

class HorizontalLayout(var gap: Float = 0f): LayoutGroup {
    data class Element(val view: View, val sizing: Sizing, val wrap: Size)
    val elements: MutableList<Element> = mutableListOf()

    var elemsMaxHeight = 0f
    var elemsWidth = 0f

    override fun measure(children: List<View>, innerC: Constraints): Size {
        elements.clear()
        elemsWidth = 0f
        elemsMaxHeight = 0f

        val loose = innerC.loosen()
        for (child in children) {
            val rect = child.require<RectTransform>()
            val size = rect.onMeasure(loose)
            elemsMaxHeight = maxOf(size.h, elemsMaxHeight)
            elements += Element(child, rect.sizing, size)
            elemsWidth += size.w
        }
        val gaps = gap * (children.size - 1).coerceAtLeast(0)

        return Size(
            (gaps + elemsWidth).coerceIn(innerC.minW, innerC.maxW),
            elemsMaxHeight.coerceIn(innerC.minH, innerC.maxH)
        )
    }

    override fun arrange(
        children: List<View>,
        innerRect: Rect
    ) {
        val gaps = (gap * (elements.size - 1)).coerceAtLeast(0f)
        val freeSpace = innerRect.w - gaps - elemsWidth

        val flex = elements.filter { it.sizing.sizingWidth == SizingPolicy.MATCH_PARENT }
        val bonusPerFlex = if (flex.isNotEmpty()) freeSpace / flex.size else 0f

        var accumulatedX = innerRect.x
        elements.forEachIndexed { i, elem ->
            val rt = elem.view.require<RectTransform>()

            val width = when(rt.sizing.sizingWidth) {
                SizingPolicy.WRAP -> elem.wrap.w
                SizingPolicy.MATCH_PARENT -> (elem.wrap.w + bonusPerFlex)
                    .coerceIn(rt.minW, rt.maxW)
            }

            val height = when (rt.sizing.sizingHeight) {
                SizingPolicy.WRAP -> minOf(elem.wrap.h, innerRect.h)
                SizingPolicy.MATCH_PARENT -> innerRect.h
            }

            val rect = Rect(accumulatedX, innerRect.y, width, height)
            rt.onArrange(rect, false)
            accumulatedX += width + if (i < elements.size - 1) gap else 0f
        }
    }
}