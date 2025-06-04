package org.qbrp.client.engine.items.components.tooltip

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import org.qbrp.main.core.utils.networking.messages.components.readonly.ClusterViewer

class ProgressBarTooltip : DynamicTooltip() {
    private var animationEnabled: Boolean = false
    private var progress: Double = 0.0
    private var startTimeMs: Long = 0L
    private val animationDurationMs: Long = 2000L

    override fun onMessage(id: String, content: ClusterViewer) {
        if (id == "description.read") {
            progress = 0.0
            startTimeMs = System.currentTimeMillis()
            animationEnabled = true
        }
    }

    override fun getHeight(): Int = 8

    override fun getWidth(textRenderer: TextRenderer): Int = 100

    override fun drawItems(textRenderer: TextRenderer, x: Int, y: Int, context: DrawContext) {
        if (animationEnabled) {
            val now = System.currentTimeMillis()
            val elapsed = now - startTimeMs
            val fraction = (elapsed.toDouble() / animationDurationMs).coerceIn(0.0, 1.0)
            progress = fraction * 100.0
            if (elapsed >= animationDurationMs) {
                progress = 100.0
                animationEnabled = false
            }
        }

        context.fill(x, y, x + 100, y + 8, 0xFF555555.toInt())
        val barWidth = progress.coerceIn(0.0, 100.0).toInt()
        context.fill(x, y, x + barWidth, y + 8, 0xFF00FF00.toInt())
    }
}