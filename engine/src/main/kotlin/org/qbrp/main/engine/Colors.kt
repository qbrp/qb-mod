package org.qbrp.main.engine

object Colors {
    val WHITE = rgb(255, 255, 255)
    val GRAY = rgb(170, 170, 170)
    val GREEN = rgb(85, 255, 85)
    val RED = rgb(255, 85, 85)
    val RED_DIM = rgba(255, 55, 55, 80)
    val GREEN_LIGHT = rgb(0, 240, 0)

    val PANEL_GRAY = rgb(30, 30, 30)

    val CROSSHAIR = WHITE

    val BACKGROUND = rgba(34, 34, 34, 204)
    val BACKGROUND_LIGHTED = rgba(85, 85, 85, 255)
    val BACKGROUND_DARK = rgba(14, 14, 14, 204)
    val BACKGROUND_UNAVAILABLE = rgba(136, 34, 34, 204)

    fun rgb(r: Int, g: Int, b: Int): Int {
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
    }
    fun rgba(r: Int, g: Int, b: Int, a: Int): Int {
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
    fun black(rgb: Int): Int {
        return (0xFF shl 24) or (rgb shl 16) or (rgb shl 8) or rgb
    }

    fun lerp(c1: Int, c2: Int, t: Float): Int {
        val tt = t.coerceIn(0f, 1f)

        val a1 = (c1 ushr 24) and 0xFF
        val r1 = (c1 ushr 16) and 0xFF
        val g1 = (c1 ushr 8) and 0xFF
        val b1 = c1 and 0xFF

        val a2 = (c2 ushr 24) and 0xFF
        val r2 = (c2 ushr 16) and 0xFF
        val g2 = (c2 ushr 8) and 0xFF
        val b2 = c2 and 0xFF

        val a = (a1 + (a2 - a1) * tt).toInt()
        val r = (r1 + (r2 - r1) * tt).toInt()
        val g = (g1 + (g2 - g1) * tt).toInt()
        val b = (b1 + (b2 - b1) * tt).toInt()

        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}