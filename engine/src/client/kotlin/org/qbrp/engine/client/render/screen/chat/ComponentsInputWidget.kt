package org.qbrp.engine.client.render.screen.chat

import icyllis.arc3d.core.Color
import icyllis.modernui.core.Context
import icyllis.modernui.graphics.Canvas
import icyllis.modernui.graphics.text.FontMetricsInt
import icyllis.modernui.text.Editable
import icyllis.modernui.text.SpannableStringBuilder
import icyllis.modernui.text.TextDirectionHeuristics
import icyllis.modernui.text.TextPaint
import icyllis.modernui.text.TextShaper
import icyllis.modernui.text.TextWatcher
import icyllis.modernui.text.style.ReplacementSpan
import icyllis.modernui.widget.EditText
import org.qbrp.engine.client.engine.chat.system.ChatTextTransformer
import org.qbrp.system.utils.format.Format.formatAsSpans

class ComponentsInputWidget(context: Context) : EditText(context) {
    companion object { val transformer = ChatTextTransformer() }
    private var isUpdating = false
    private val padding: Int by lazy { dpToPx(8) }

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true

                val originalText = s?.toString() ?: ""
                val currentSelection = selectionStart.takeIf { it != -1 } ?: 0

                processTags()
                val transformedText = transformer.getTransformedMessage(originalText)

                // Проверяем, действительно ли текст изменился
                if (text.toString() != transformedText) {
                    setText(transformedText.formatAsSpans())
                    // Учитываем смещение курсора после трансформации
                    val newLength = transformedText.length
                    val selectionOffset = transformedText.length - originalText.length
                    val newSelection = (currentSelection + selectionOffset).coerceIn(0, newLength)

                    if (newLength > 0) {
                        setSelection(newSelection)
                    }
                }

                isUpdating = false
            }


            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun dpToPx(dp: Int): Int =
        (dp * context.resources.displayMetrics.density).toInt()

    private fun measureTextWidth(paint: TextPaint, text: String): Float =
        text.length * paint.textSize * 0.5f

    private fun processTags() {
        val originalText = text.toString()
        val spannable = SpannableStringBuilder(originalText)
        val regex = Regex("""<(\w+):"(.*?)">""")

        regex.findAll(originalText).forEach { match ->
            val tag = match.groupValues[1]
            val value = match.groupValues[2]
            val tagName = MessageComponentsAssociations.get(tag) ?: tag
            val displayedText = "$tagName: $value"
            spannable.setSpan(
                createTagSpan(displayedText),
                match.range.first,
                match.range.last + 1,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        setText(spannable)
        setSelection(spannable.length)
    }

    private fun createTagSpan(displayedText: String) = object : ReplacementSpan() {
        override fun getSize(
            paint: TextPaint,
            text: CharSequence?,
            start: Int,
            end: Int,
            fm: FontMetricsInt?
        ): Int {
            return (measureTextWidth(paint, displayedText) + padding * 2).toInt()
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence?,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: TextPaint
        ) {
            val originalColor = paint.color

            // Рисуем фон: прозрачный, менее яркий синий (alpha=128, rgb=(0, 0, 190))
            paint.color = Color.argb(128, 0, 0, 190)
            val textWidth = measureTextWidth(paint, displayedText)
            canvas.drawRoundRect(
                x,
                top.toFloat(),
                x + textWidth + padding,
                bottom.toFloat(),
                padding.toFloat(),
                padding,
                paint
            )

            // Рисуем текст поверх фона белым цветом
            paint.color = Color.WHITE
            val shapedText = TextShaper.shapeText(
                displayedText, 0, displayedText.length,
                TextDirectionHeuristics.LTR, paint
            )
            canvas.drawShapedText(shapedText, x + padding, y.toFloat(), paint)
            paint.color = originalColor
        }
    }
}
