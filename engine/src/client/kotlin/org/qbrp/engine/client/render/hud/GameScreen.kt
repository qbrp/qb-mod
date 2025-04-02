package org.qbrp.engine.client.render.hud

import icyllis.modernui.animation.ObjectAnimator
import icyllis.modernui.animation.TimeInterpolator
import icyllis.modernui.animation.ValueAnimator
import icyllis.modernui.fragment.Fragment
import icyllis.modernui.mc.ScreenCallback
import icyllis.modernui.util.DataSet
import icyllis.modernui.view.Gravity
import icyllis.modernui.view.LayoutInflater
import icyllis.modernui.view.View
import icyllis.modernui.view.ViewGroup
import icyllis.modernui.widget.FrameLayout
import icyllis.modernui.widget.TextView

class GameScreen : Fragment(), ScreenCallback {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: DataSet?
    ): View? {
        val frameLayout = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val textView = TextView(requireContext()).apply {
            text = "Текст для теста" // Устанавливаем текст
            setTextSize(24f)        // Задаем размер шрифта
            setTextColor(0xFFFFFFFF.toInt()) // Устанавливаем белый цвет
        }

        frameLayout.addView(
            textView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER // Центрируем текст
            }
        )

        // Добавляем бесконечную анимацию для TextView
        val animator = ObjectAnimator.ofFloat(textView, View.TRANSLATION_X, -100f, 100f).apply {
            duration = 2000 // Длительность анимации (2 секунды)
            repeatCount = ValueAnimator.INFINITE // Бесконечное повторение
            repeatMode = ValueAnimator.REVERSE // Возврат в обратном направлении
            interpolator = TimeInterpolator.LINEAR // Линейное движение
            start() // Запускаем анимацию
        }

        return frameLayout
    }

    override fun hasDefaultBackground() = false
    override fun shouldClose() = false
    override fun shouldBlurBackground() = false
}