package org.qbrp.engine.client.render.hud

import icyllis.modernui.animation.ObjectAnimator
import icyllis.modernui.animation.TimeInterpolator
import icyllis.modernui.core.Context
import icyllis.modernui.graphics.Canvas
import icyllis.modernui.graphics.Paint
import icyllis.modernui.graphics.drawable.Drawable
import icyllis.modernui.view.MeasureSpec
import icyllis.modernui.view.View
import icyllis.modernui.widget.FrameLayout
import icyllis.modernui.widget.LinearLayout
import icyllis.modernui.widget.TextView
import net.minecraft.client.MinecraftClient
import kotlin.math.atan2

class TooltipView(val screen: GameScreen) : LinearLayout(screen.requireContext(), ) {
    val components: MutableList<View> = mutableListOf()
    var textSizeMultiplier: Float = (screen.sizeMultiplier * 1.5).toFloat()
    init {
        orientation = VERTICAL
    }

    fun build() {
        components.forEach {
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                bottomMargin = 5
                marginStart = 5
            }
            addView(it)
            requestLayout()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint.obtain()
        // Рисуем границы
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint.apply {
            style = Paint.Style.STROKE.ordinal
            strokeWidth = 2f
            color = 0xFFFFFFFF.toInt()
        })
    }

    fun startOpeningAnimation(initialParams: LinearLayout.LayoutParams) {
        val (targetWidth, targetHeight) = measureSize()
        val animator = ObjectAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = TimeInterpolator.DECELERATE
            addUpdateListener {
                val progress = it.animatedValue as Float
                val currentWidth = (targetWidth * progress).toInt().coerceAtLeast(1)
                val currentHeight = (targetHeight * progress).toInt().coerceAtLeast(1)

                layoutParams = LinearLayout.LayoutParams(currentWidth, currentHeight).apply {
                    // Так же можно скопировать нужные поля из initialParams
                    bottomMargin = (initialParams.bottomMargin)
                    // При необходимости другие поля: leftMargin, topMargin и т.д.
                }
                requestLayout()
            }
        }
        animator.start()
    }


    fun measureSize(): Pair<Int, Int> {
        val specWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val specHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        measure(specWidth, specHeight)
        return measuredWidth to measuredHeight
    }

}