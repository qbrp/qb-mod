package org.qbrp.engine.client.render.hud

import icyllis.modernui.animation.ObjectAnimator
import icyllis.modernui.animation.TimeInterpolator
import icyllis.modernui.core.Core
import icyllis.modernui.fragment.Fragment
import icyllis.modernui.mc.ScreenCallback
import icyllis.modernui.util.DataSet
import icyllis.modernui.view.Gravity
import icyllis.modernui.view.LayoutInflater
import icyllis.modernui.view.View
import icyllis.modernui.view.ViewGroup
import icyllis.modernui.view.ViewGroup.LayoutParams.MATCH_PARENT
import icyllis.modernui.view.ViewGroup.LayoutParams.WRAP_CONTENT
import icyllis.modernui.widget.FrameLayout
import icyllis.modernui.widget.GridLayout
import icyllis.modernui.widget.LinearLayout
import jdk.internal.org.jline.utils.InfoCmp.Capability.columns
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import kotlin.math.cos
import kotlin.math.sin

class GameScreen : Fragment(), ScreenCallback {
    private val windows = mutableListOf<TooltipView>()

    // Базовые размеры в dp
    private val BASE_WIDTH_DP = 200f
    private val BASE_HEIGHT_DP = 100f
    private var radiusMultiplier = 1.3f
    var sizeMultiplier = 0.5f

    lateinit var screenContainer: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: DataSet?
    ): View? {

        // Инициализация контейнеров
        screenContainer = GridLayout(requireContext()).apply {
            orientation = GridLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            rowCount = 3
            columnCount = 3
        }

        val tooltip = TooltipView(this).apply {
            layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(0), // верхняя строка
                GridLayout.spec(screenContainer.columnCount - 1) ).apply {
                width = MATCH_PARENT
                height = WRAP_CONTENT
            }
            components.add(TextTooltipComponent(this, "Эдейн Руссо", TooltipTextSize.TITLE).apply {
                //setTextColor(0xdf8de1.toInt())
            })

            components.add(TextTooltipComponent(
                this,
                "Девушка с изящными и утонченными чертами лица, с лисьим разрезом глаз. Сами глаза розового цвета." +
                        "Волосы светло-русого оттенка, которые кудрями спускаются до самой поясницы. " +
                        "Особа обладала стройной фигурой с пышной грудью и бедрами.",
                TooltipTextSize.TEXT))
            background = DarkBackground(this) // Тёмный фон
            build()
        }

        //screenContainer.addView(tooltip)

        return screenContainer
    }

    fun getScaledLayoutParams(animationScale: Float): FrameLayout.LayoutParams {
        val scaleFactor = MinecraftClient.getInstance().window.scaleFactor
        return FrameLayout.LayoutParams(
            ((BASE_WIDTH_DP * sizeMultiplier * scaleFactor) * animationScale).toInt(),
            ((BASE_HEIGHT_DP * sizeMultiplier * scaleFactor) * animationScale).toInt()
        )
    }

    fun getWrapContentLayoutParams(): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
    }

    fun setWindowSizeMultiplier(newMultiplier: Float) {
        sizeMultiplier = newMultiplier.coerceAtLeast(0.1f) // Ограничение минимального размера
        updateAllWindows()
    }

    // Пересчёт размеров всех окон
    private fun updateAllWindows() {
        for (window in windows) {
            window.layoutParams = getWrapContentLayoutParams()
            window.requestLayout()
        }
    }

    override fun hasDefaultBackground() = false
    override fun shouldClose() = false
    override fun shouldBlurBackground() = false
}