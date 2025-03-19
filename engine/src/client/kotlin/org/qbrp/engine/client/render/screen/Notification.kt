package org.qbrp.engine.client.render.screen

import icyllis.arc3d.core.Color
import icyllis.modernui.animation.Animator
import icyllis.modernui.animation.AnimatorListener
import icyllis.modernui.animation.ValueAnimator
import icyllis.modernui.fragment.Fragment
import icyllis.modernui.graphics.Paint.TextStyle
import icyllis.modernui.graphics.drawable.GradientDrawable
import icyllis.modernui.mc.ScreenCallback
import icyllis.modernui.util.DataSet
import icyllis.modernui.view.Gravity
import icyllis.modernui.view.LayoutInflater
import icyllis.modernui.view.View
import icyllis.modernui.view.ViewGroup
import icyllis.modernui.widget.Button
import icyllis.modernui.widget.FrameLayout
import icyllis.modernui.widget.LinearLayout
import icyllis.modernui.widget.TextView
import org.qbrp.engine.client.render.screen.main.MainMenuScreen.Companion.FADE_ANIMATION_DURATION

class Notification(private val titleText: String, private val descText: String, val containerCallback: (Notification, LinearLayout) -> Unit) : Fragment(), ScreenCallback {

    // Контейнер для дополнительных компонентов
    private lateinit var additionalContainer: LinearLayout

    override fun hasDefaultBackground(): Boolean = false
    override fun shouldBlurBackground(): Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: DataSet?
    ): View? {
        // Корневой контейнер на весь экран с затемнённым фоном для выделения уведомления
        val rootLayout = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Можно добавить затемнённый фон, если требуется (например, полупрозрачный фон родительского окна)
            // setBackgroundColor(0x80000000.toInt())
            alpha = 0f // Изначально прозрачный для анимации fade-in
        }

        val backgroundDrawable = GradientDrawable().apply {
            setColor(Color.BLACK) // Белый фон
            cornerRadius = 8f // Закругленные углы
            alpha = 100
        }

        val dividerDrawable = GradientDrawable().apply {
            setColor(Color.LTGRAY) // Цвет линии
            setSize(1, 2) // Толщина линии
        }

        val maxWidth = (600 * requireContext().resources.displayMetrics.density).toInt() // 300dp в пикселях
        val notificationContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                maxWidth,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            setPadding(16, 16, 16, 16)
            background = backgroundDrawable // Фон уведомления

            // Верхний контейнер (30% высоты)
            val headerLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    -10 // Здесь 0 высоты, но будет регулироваться весом
                ).apply {
                    weight = 0.3f // 30% от общей высоты
                }
                gravity = Gravity.CENTER

                // Заголовок
                val title = TextView(requireContext()).apply {
                    text = titleText
                    textSize = 18f
                }

                addView(title) // Добавляем заголовок в headerLayout
            }

            // Основной контейнер с текстом и дополнительными элементами (70% высоты)
            // Основной контейнер с текстом и дополнительными элементами (70% высоты)
            val contentLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0 // 0 высоты, регулируется весом
                ).apply {
                    weight = 0.7f // 70% от общей высоты
                }

                // Описание
                val description = TextView(requireContext()).apply {
                    text = descText
                    textSize = 16f
                    gravity = Gravity.START or Gravity.TOP
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT // Расширяется по контенту
                    )
                    setSingleLine(false) // Убираем ограничение на одну строку
                    ellipsize = null
                }

                // Контейнер для дополнительных компонентов
                additionalContainer = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    addView(Button(requireContext()).apply {
                        setText("Принять")
                    })
                }

                // Добавляем описание и дополнительные элементы
                addView(description)
                addView(additionalContainer)
            }


            // Добавляем headerLayout и contentLayout в notificationContainer
            addView(headerLayout)
            addView(contentLayout)
        }


        // Добавляем уведомление в корневой контейнер
        rootLayout.addView(notificationContainer)

        return rootLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: DataSet?) {
        super.onViewCreated(view, savedInstanceState)
        // Анимация появления уведомления (fade-in)
        val fadeIn = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = FADE_ANIMATION_DURATION
            addUpdateListener { animator ->
                view.alpha = animator.animatedValue as Float
            }
        }
        fadeIn.start()
    }


    fun addComponent(component: View) {
        additionalContainer.addView(component)
    }
}
