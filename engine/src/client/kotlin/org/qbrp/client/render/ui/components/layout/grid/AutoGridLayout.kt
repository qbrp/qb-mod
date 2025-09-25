package org.qbrp.client.render.ui.components.layout.grid

import org.litote.kmongo.util.idValue
import org.qbrp.client.render.ui.components.layout.Constraints
import org.qbrp.client.render.ui.components.layout.LayoutGroup
import org.qbrp.client.render.ui.components.layout.Rect
import org.qbrp.client.render.ui.components.layout.Size
import org.qbrp.client.render.ui.components.layout.SizingPolicy
import org.qbrp.client.render.ui.components.transform.RectTransform
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent
import org.qbrp.client.render.ui.view.ViewContext
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.core.utils.sumOf

class AutoGridLayout(
    ctx: ViewContext,
    var cols: Int,
    var colGap: Float = 0f,
    var rowGap: Float = 0f,
    var colMin: FloatArray? = null
) : ViewComponent(ctx), LayoutGroup {

    data class Elem(val view: View, val gp: GridPosition, val wrap: Size)

    val elems = mutableListOf<Elem>()

    var rowsUsed = 0
        private set
    lateinit var colW: FloatArray
        private set
    lateinit var rowH: FloatArray
        private set

    fun elemAt(x: Float, y: Float): Elem? {
        for (i in elems.lastIndex downTo 0) {
            val children = elems[i]
            val sx = children.view.x
            val sy = children.view.y
            val ex = sx + children.view.width
            val ey = sy + children.view.height
            if (x >= sx && x < ex && y >= sy && y < ey) {
                return children
            }
        }
        return null
    }

    fun rowSize(row: Int): Size {
        return Size(
            elemsAtRow(row).sumOf { it.view.width },
            rowH[row]
        )
    }

    fun elemsAtRow(row: Int): List<Elem> {
        return elems.filter { it.gp.row == row }
    }

    override fun measure(children: List<View>, innerC: Constraints): Size {
        elems.clear()
        rowsUsed = 0

        // 0) собрать детей и их GridPosition; авто-раскладка по row-major, если не задано
        var autoIdx = 0
        val loose = innerC.loosen()
        for (child in children) {
            val rt = child.require<RectTransform>()
            val gp = child.get<GridPosition>() ?: GridPosition(child.componentContext).also {
                child.add(it)
                it.row = autoIdx / cols
                it.col = autoIdx % cols
                autoIdx++
            }
            gp.colSpan = gp.colSpan.coerceAtLeast(1)
            gp.rowSpan = gp.rowSpan.coerceAtLeast(1)

            // меряем WRAP ребёнка «в теории»: максимум области его спана
            val maxW = gp.colSpan * 1e6f // достаточно большое число; можно оценить от innerC.maxW
            val maxH = gp.rowSpan * 1e6f
            val wrap = rt.onMeasure(Constraints(0f, 0f, maxW, maxH))

            elems += Elem(child, gp, wrap)
            rowsUsed = maxOf(rowsUsed, gp.row + gp.rowSpan)
        }

        if (cols <= 0) return Size(0f, 0f)

        // 1) базовые массивы треков
        colW = FloatArray(cols) { 0f }
        rowH = FloatArray(maxOf(rowsUsed, 0)) { 0f }

        // учтём минимумы колонок (например, иконка = 8)
        colMin?.let { mins ->
            for (i in 0 until minOf(cols, mins.size)) colW[i] = maxOf(colW[i], mins[i])
        }

        // 2) одиночные спаны: colSpan==1 → обновляем ширину колонки, rowSpan==1 → высоту строки
        for (e in elems) {
            if (e.gp.colSpan == 1) colW[e.gp.col] = maxOf(colW[e.gp.col], e.wrap.w)
            if (e.gp.rowSpan == 1) {
                // увеличим rowH до нужного индекса
                if (e.gp.row >= rowH.size) {
                    rowH = rowH.copyOf(e.gp.row + 1)
                }
                rowH[e.gp.row] = maxOf(rowH[e.gp.row], e.wrap.h)
            }
        }

        // гарантируем длину rowH (если не было одиночных спанов)
        if (rowH.size < rowsUsed) rowH = rowH.copyOf(rowsUsed)

        // 3) многоячейковые спаны: дотащить дефицит в последний трек спана (простой, но рабочий приоритет)
        fun sumCols(from: Int, span: Int): Float {
            var s = 0f
            for (i in 0 until span) s += colW[from + i]
            s += colGap * (span - 1).coerceAtLeast(0)
            return s
        }
        fun sumRows(from: Int, span: Int): Float {
            var s = 0f
            for (i in 0 until span) s += rowH[from + i]
            s += rowGap * (span - 1).coerceAtLeast(0)
            return s
        }

        for (e in elems) {
            if (e.gp.colSpan > 1) {
                val have = sumCols(e.gp.col, e.gp.colSpan)
                val need = e.wrap.w
                if (need > have) {
                    val last = e.gp.col + e.gp.colSpan - 1
                    colW[last] += (need - have) // можно распределять равномерно, но это проще
                }
            }
            if (e.gp.rowSpan > 1) {
                val have = sumRows(e.gp.row, e.gp.rowSpan)
                val need = e.wrap.h
                if (need > have) {
                    val last = e.gp.row + e.gp.rowSpan - 1
                    rowH[last] += (need - have)
                }
            }
        }

        val totalW = colW.sum() + colGap * (cols - 1).coerceAtLeast(0)
        val totalH = rowH.sum() + rowGap * (rowH.size - 1).coerceAtLeast(0)

        return Size(
            totalW.coerceIn(innerC.minW, innerC.maxW),
            totalH.coerceIn(innerC.minH, innerC.maxH)
        )
    }

    override fun arrange(children: List<View>, innerRect: Rect) {
        if (cols == 0) return
        // префикс-суммы по колонкам/строкам
        val xOffsets = FloatArray(cols) { 0f }
        var acc = innerRect.x
        for (c in 0 until cols) {
            xOffsets[c] = acc
            acc += colW[c]
            if (c < cols - 1) acc += colGap
        }

        val rows = rowH.size
        val yOffsets = FloatArray(rows) { 0f }
        acc = innerRect.y
        for (r in 0 until rows) {
            yOffsets[r] = acc
            acc += rowH[r]
            if (r < rows - 1) acc += rowGap
        }

        // раскладываем детей
        for (e in elems) {
            val rt = e.view.require<RectTransform>()
            val c0 = e.gp.col
            val r0 = e.gp.row
            val cSpan = e.gp.colSpan
            val rSpan = e.gp.rowSpan

            // границы спана
            val x = xOffsets.getOrElse(c0) { innerRect.x }
            val y = yOffsets.getOrElse(r0) { innerRect.y }

            var spanW = 0f
            for (i in 0 until cSpan) {
                val idx = c0 + i
                spanW += colW.getOrElse(idx) { 0f }
                if (i < cSpan - 1) spanW += colGap
            }

            var spanH = 0f
            for (i in 0 until rSpan) {
                val idx = r0 + i
                spanH += rowH.getOrElse(idx) { 0f }
                if (i < rSpan - 1) spanH += rowGap
            }

            // уважим sizing: WRAP ограничиваем треком, MATCH_PARENT — растягиваем
            val w = when (rt.sizing.sizingWidth) {
                SizingPolicy.MATCH_PARENT -> spanW.coerceIn(rt.minW, rt.maxW)
                SizingPolicy.WRAP         -> minOf(e.wrap.w, spanW).coerceIn(rt.minW, rt.maxW)
            }
            val h = when (rt.sizing.sizingHeight) {
                SizingPolicy.MATCH_PARENT -> spanH.coerceIn(rt.minH, rt.maxH)
                SizingPolicy.WRAP         -> minOf(e.wrap.h, spanH).coerceIn(rt.minH, rt.maxH)
            }

            // кладём к левому/верхнему края спана (при желании можно добавить Align)
            rt.onArrange(Rect(x, y, w, h), /*applyAnchors=*/false)
        }
    }
}