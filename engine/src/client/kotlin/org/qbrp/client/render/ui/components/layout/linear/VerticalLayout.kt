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

enum class VerticalDirection { DOWN, UP }

class VerticalLayout(
    var gap: Float = 0f,
    var direction: VerticalDirection = VerticalDirection.DOWN
): LayoutGroup {
    data class Element(val view: View, val sizing: Sizing, val wrap: Size)
    val elements: MutableList<Element> = mutableListOf()

    var elemsHeight = 0f
    var elemsMaxWidth = 0f

    override fun measure(children: List<View>, innerC: Constraints): Size {
        elements.clear()
        elemsHeight = 0f
        elemsMaxWidth = 0f

        val loose = innerC.loosen()
        for (child in children) {
            val rect = child.require<RectTransform>()
            val size = rect.onMeasure(loose)
            elemsMaxWidth = maxOf(size.w, elemsMaxWidth)
            elements += Element(child, rect.sizing, size)
            elemsHeight += size.h
        }
        val gaps = gap * (children.size - 1).coerceAtLeast(0)

        return Size(
            elemsMaxWidth.coerceIn(innerC.minW, innerC.maxW),
            (gaps + elemsHeight).coerceIn(innerC.minH, innerC.maxH)
        )
    }

    override fun arrange(
        children: List<View>,
        innerRect: Rect
    ) {
        val gaps = (gap * (elements.size - 1)).coerceAtLeast(0f)
        val freeSpace = innerRect.h - gaps - elemsHeight

        val flex = elements.filter { it.sizing.sizingHeight == SizingPolicy.MATCH_PARENT }
        val bonusPerFlex = if (flex.isNotEmpty()) freeSpace / flex.size else 0f

        var y = when (direction) {
            VerticalDirection.DOWN -> innerRect.y
            VerticalDirection.UP -> innerRect.y + innerRect.h
        }

        elements.forEachIndexed { i, elem ->
            val rt = elem.view.require<RectTransform>()

            val width = when (rt.sizing.sizingWidth) {
                SizingPolicy.WRAP -> minOf(elem.wrap.w, innerRect.w)
                SizingPolicy.MATCH_PARENT -> innerRect.w
            }

            val height = when(rt.sizing.sizingHeight) {
                SizingPolicy.WRAP -> elem.wrap.h
                SizingPolicy.MATCH_PARENT -> (elem.wrap.h + bonusPerFlex)
                    .coerceIn(rt.minH, rt.maxH)
            }

            val rect = when (direction) {
                VerticalDirection.DOWN -> Rect(innerRect.x, y, width, height)
                VerticalDirection.UP -> {
                    y -= height
                    Rect(innerRect.x, y, width, height)
                }
            }
            rt.onArrange(rect, false)
            when (direction) {
                VerticalDirection.DOWN -> y += height + if (i < elements.size - 1) gap else 0f
                VerticalDirection.UP -> y -= if (i < elements.size - 1) gap else 0f
            }
        }
    }
}