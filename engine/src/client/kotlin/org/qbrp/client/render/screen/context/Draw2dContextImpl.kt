package org.qbrp.client.render.screen.context

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.ScreenRect
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper
import org.joml.Matrix4f
import org.qbrp.client.render.Render
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.dataformats.vec2
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.client.render.item.model.render.ItemRenderer
import org.qbrp.client.render.screen.Alpha
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.main.engine.text.components.TextComponent
import kotlin.math.max

class Draw2dContextImpl(
    private val mc: DrawContext,
    override val tickDelta: Float,
    private val itemRenderer: ItemRenderer
) : Draw2dContext {
    override val matrices: MatrixStack
        get() = mc.matrices
    override val vertexConsumers: VertexConsumerProvider.Immediate
        get() = mc.vertexConsumers

    override fun beginAlpha(clear: Boolean) = Alpha.begin(clear)

    override fun endAlpha() = Alpha.end()

    override fun drawAlpha(alpha: Float) = Alpha.draw(this.mc, alpha)

    override fun disposeAlpha() = Alpha.dispose()

    override fun enableScissor(x1: Float, y1: Float, x2: Float, y2: Float) {
        mc.enableScissor(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
    }

    override fun disableScissor() {
        mc.disableScissor()
    }

    override fun drawBorder(x: Float, y: Float, width: Float, height: Float, z: Float, color: Int) {
        this.fill(x, y, x + width, y + 1, z, color)
        this.fill(x, y + height - 1, x + width, y + height, z, color)
        this.fill(x, y + 1, x + 1, y + height - 1, z, color)
        this.fill(x + width - 1, y + 1, x + width, y + height - 1, z, color)
    }

    override fun draw() {
        RenderSystem.disableDepthTest();
        vertexConsumers.draw();
        RenderSystem.enableDepthTest();
    }

    override fun drawItem(display: ItemDisplay, x: Float, y: Float, scale: Float, z: Float) {
        itemRenderer.renderItem2d(this, display, vec2(x, y), scale, z)
    }

    override fun fillGradientVertical(x1: Float, y1: Float, x2: Float, y2: Float, z: Float, startColor: Int, endColor: Int) {
        this.fillGradient(RenderLayer.getGui(), x1, y1, x2, y2, startColor, endColor, z, GradientDir.VERTICAL)
    }

    override fun fillGradientHorizontal(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        z: Float,
        startColor: Int,
        endColor: Int
    ) {
        this.fillGradient(RenderLayer.getGui(), x1, y1, x2, y2, startColor, endColor, z, GradientDir.HORIZONTAL)
    }

    enum class GradientDir { VERTICAL, HORIZONTAL }

    fun fillGradient(
        layer: RenderLayer?,
        startX: Float, startY: Float,
        endX: Float, endY: Float,
        colorStart: Int, colorEnd: Int,
        z: Float,
        dir: GradientDir
    ) {
        val vc = this.vertexConsumers.getBuffer(layer)
        fun c(c: Int) = floatArrayOf(
            ColorHelper.Argb.getRed(c) / 255f,
            ColorHelper.Argb.getGreen(c) / 255f,
            ColorHelper.Argb.getBlue(c) / 255f,
            ColorHelper.Argb.getAlpha(c) / 255f
        )
        val s = c(colorStart)
        val e = c(colorEnd)
        val m = this.matrices.peek().positionMatrix

        when (dir) {
            GradientDir.VERTICAL -> {
                vc.vertex(m, startX, startY, z).color(s[0], s[1], s[2], s[3]).next() // TL
                vc.vertex(m, startX, endY,   z).color(e[0], e[1], e[2], e[3]).next() // BL
                vc.vertex(m, endX,   endY,   z).color(e[0], e[1], e[2], e[3]).next() // BR
                vc.vertex(m, endX,   startY, z).color(s[0], s[1], s[2], s[3]).next() // TR
            }
            GradientDir.HORIZONTAL -> {
                vc.vertex(m, startX, startY, z).color(s[0], s[1], s[2], s[3]).next() // TL
                vc.vertex(m, startX, endY,   z).color(s[0], s[1], s[2], s[3]).next() // BL
                vc.vertex(m, endX,   endY,   z).color(e[0], e[1], e[2], e[3]).next() // BR
                vc.vertex(m, endX,   startY, z).color(e[0], e[1], e[2], e[3]).next() // TR
            }
        }
    }

    override fun drawTexturedQuad(
        texture: Identifier?,
        x1: Float,
        x2: Float,
        y1: Float,
        y2: Float,
        z: Float,
        u1: Float,
        u2: Float,
        v1: Float,
        v2: Float
    ) {
        RenderSystem.setShaderTexture(0, texture)
        RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
        val matrix4f = this.matrices.peek().getPositionMatrix()
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(matrix4f, x1.toFloat(), y1.toFloat(), z.toFloat()).texture(u1, v1).next()
        bufferBuilder.vertex(matrix4f, x1.toFloat(), y2.toFloat(), z.toFloat()).texture(u1, v2).next()
        bufferBuilder.vertex(matrix4f, x2.toFloat(), y2.toFloat(), z.toFloat()).texture(u2, v2).next()
        bufferBuilder.vertex(matrix4f, x2.toFloat(), y1.toFloat(), z.toFloat()).texture(u2, v1).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
    }

    override fun fillWhiteTexture(x1: Float, y1: Float, x2: Float, y2: Float, z: Float) {
        RenderSystem.setShaderTexture(0, Render.WHITE_FILL)
        RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
        val matrix4f: Matrix4f? = this.matrices.peek().getPositionMatrix()
        val bufferBuilder = Tessellator.getInstance().buffer
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        bufferBuilder.vertex(matrix4f, x1.toFloat(), y1.toFloat(), z).texture(0f, 0f).next()
        bufferBuilder.vertex(matrix4f, x1.toFloat(), y2.toFloat(), z).texture(0f, 1f).next()
        bufferBuilder.vertex(matrix4f, x2.toFloat(), y2.toFloat(), z).texture(1f, 1f).next()
        bufferBuilder.vertex(matrix4f, x2.toFloat(), y1.toFloat(), z).texture(1f, 0f).next()
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
    }

    override fun fill(x1: Float, y1: Float, x2: Float, y2: Float, z: Float, color: Int) {
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        val matrix4f: Matrix4f? = mc.matrices.peek().getPositionMatrix()
        if (x1 < x2) {
            val i = x1
            x1 = x2
            x2 = i
        }

        if (y1 < y2) {
            val i = y1
            y1 = y2
            y2 = i
        }

        val f = ColorHelper.Argb.getAlpha(color).toFloat() / 255.0f
        val g = ColorHelper.Argb.getRed(color).toFloat() / 255.0f
        val h = ColorHelper.Argb.getGreen(color).toFloat() / 255.0f
        val j = ColorHelper.Argb.getBlue(color).toFloat() / 255.0f
        val vertexConsumer: VertexConsumer = mc.vertexConsumers.getBuffer(RenderLayer.getGui())
        vertexConsumer.vertex(matrix4f, x1, y1, z).color(g, h, j, f).next()
        vertexConsumer.vertex(matrix4f, x1, y2, z).color(g, h, j, f).next()
        vertexConsumer.vertex(matrix4f, x2, y2, z).color(g, h, j, f).next()
        vertexConsumer.vertex(matrix4f, x2, y1, z).color(g, h, j, f).next()
        draw()
    }


    override fun drawText(textRenderer: TextRenderer, text: TextComponent, pos: Vec2, z: Float): Int {
        val i = textRenderer.render(
            matrices,
            vertexConsumers,
            text,
            pos
        )
        draw()
        return i
    }

}