package org.qbrp.engine.client.render.hud

import icyllis.modernui.graphics.Canvas
import icyllis.modernui.graphics.Paint
import icyllis.modernui.graphics.Rect
import icyllis.modernui.graphics.drawable.Drawable
import icyllis.modernui.view.View
import java.awt.Color

class DarkBackground(view: View): Drawable() {
    private val radius = view.dp(16F)
    private var color = Color.BLACK.rgb

    override fun draw(canvas: Canvas) {
        val rect = getBounds()
        val stroke: Float = (radius * 0.25).toFloat()
        val start: Float = stroke * 0.5f
        val paint = Paint.obtain()
        paint.color = color
        canvas.drawRect(rect.left + start, rect.top + start, rect.right - start, rect.bottom - start, paint)
        paint.recycle()
    }
}