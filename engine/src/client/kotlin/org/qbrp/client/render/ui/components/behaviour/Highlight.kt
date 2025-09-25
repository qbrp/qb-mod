package org.qbrp.client.render.ui.components.behaviour

import net.minecraft.util.math.ColorHelper
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.view.ViewComponent

class Highlight(colors: HighlightParams = HighlightParams.DEFAULT, padding: Float = 0f): HoverListener {
    private val highlighter = Highlighter(colors)

    override fun onNotHover(ctx: Draw2dContext, view: View) {
        highlighter.updateProgress(ProgressMode.DECREASE)
    }

    override fun onHover(view: View, component: ViewComponent) {
        highlighter.updateProgress(ProgressMode.INCREASE)
    }

    override fun render(ctx: Draw2dContext, view: View) {
        highlighter.render(ctx, 0f, 0f, view.width, view.height)
    }
}

data class HighlightParams(
    val highlightColorStart: Int = DEFAULT.highlightColorStart,
    val highlightColorEnd: Int = DEFAULT.highlightColorEnd ,
    val hoverSpeed: Float = DEFAULT.hoverSpeed,
    val padding: Float = 0f
) {
    companion object {
        val DEFAULT = HighlightParams(0x00FFFFFF, 0x40FFFFFF, 0.17f)
    }
}

enum class ProgressMode {
    INCREASE, DECREASE
}

class Highlighter(var params: HighlightParams) {
    private var hoverProgress: Float = 0f

    fun updateProgress(mode: ProgressMode) {
        hoverProgress = when(mode) {
            ProgressMode.DECREASE -> (hoverProgress - params.hoverSpeed).coerceAtLeast(0f)
            ProgressMode.INCREASE -> (hoverProgress + params.hoverSpeed).coerceAtMost(1f)
        }
    }

    fun render(ctx: Draw2dContext, x: Float, y: Float, width: Float, height: Float) {
        val t = hoverProgress.coerceIn(0f, 1f)
        val padding = params.padding

        val mixed = ColorHelper.Argb.lerp(
            t,
            params.highlightColorStart or 0xFF000000.toInt(),
            params.highlightColorEnd or 0xFF000000.toInt()
        )

        val rgb = mixed and 0x00FFFFFF
        val a = ((t * 0x40).toInt()).coerceIn(0, 0xFF) // 0..64
        val argb = (a shl 24) or rgb

        ctx.fill(x + padding, y + padding, x + width - padding, y + height - padding, argb)
    }
}