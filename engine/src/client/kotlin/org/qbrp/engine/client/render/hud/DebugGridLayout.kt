package org.qbrp.engine.client.render.hud

import icyllis.modernui.core.Context
import icyllis.modernui.graphics.Canvas
import icyllis.modernui.graphics.Paint
import icyllis.modernui.widget.GridLayout

class DebugGridLayout(context: Context) : GridLayout(context) {

    private val debugPaint = Paint.obtain().apply {
        color = 0xFFFF0000.toInt() // красный цвет
        strokeWidth = 1f
        // Обычно для рисования линий используют стиль STROKE, но если API не поддерживает, можно установить другое
        style = Paint.Style.STROKE.ordinal
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        // Получаем размеры всего контейнера
        val totalWidth = width.toFloat()
        val totalHeight = height.toFloat()
        val numCols = columnCount
        val numRows = rowCount

        // Для простоты, рисуем линии равномерно разделяющие контейнер
        val cellWidth = totalWidth / numCols
        val cellHeight = totalHeight / numRows

        // Рисуем вертикальные линии
        for (i in 1 until numCols) {
            val x = i * cellWidth
            canvas.drawLine(x, 0f, x, totalHeight, debugPaint)
        }

        // Рисуем горизонтальные линии
        for (j in 1 until numRows) {
            val y = j * cellHeight
            canvas.drawLine(0f, y, totalWidth, y, debugPaint)
        }
    }
}
