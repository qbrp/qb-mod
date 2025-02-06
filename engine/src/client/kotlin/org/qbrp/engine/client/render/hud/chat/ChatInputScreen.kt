package org.qbrp.engine.client.render.hud.chat

import com.vladsch.flexmark.parser.internal.CommonmarkInlineParser
import icyllis.modernui.fragment.Fragment
import icyllis.modernui.graphics.drawable.ShapeDrawable
import icyllis.modernui.mc.ScreenCallback
import icyllis.modernui.text.Editable
import icyllis.modernui.util.DataSet
import icyllis.modernui.view.Gravity
import icyllis.modernui.view.LayoutInflater
import icyllis.modernui.view.View
import icyllis.modernui.view.ViewGroup
import icyllis.modernui.widget.EditText
import icyllis.modernui.widget.FrameLayout
import icyllis.modernui.widget.LinearLayout
import icyllis.modernui.widget.LinearLayout.LayoutParams
import org.commonmark.renderer.markdown.MarkdownRenderer
import org.qbrp.engine.client.render.Colors
import org.qbrp.engine.client.render.MarkdownParser

class ChatInputScreen : Fragment(), ScreenCallback {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: DataSet?
    ): View? {
        val rootLayout = FrameLayout(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val chatContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = createBgDrawable(Colors.DARK_BACKGROUND)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM // Закрепляем внизу экрана
            }
            setPadding(7, 7, 7, 7) // Внешние отступы
        }

        val editText = EditText(context).apply {
            hint = "Введите сообщение..."
            setHintTextColor(Colors.black(240))
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }

        chatContainer.addView(editText)
        rootLayout.addView(chatContainer)

        return rootLayout
    }


    fun createBgDrawable(color: Int): ShapeDrawable {
        return ShapeDrawable().apply {
            shape = ShapeDrawable.RECTANGLE
            setColor(color)
            setPadding(5, 5, 5, 5)
        }
    }

    override fun hasDefaultBackground(): Boolean = false
    override fun shouldBlurBackground(): Boolean = false
}
