package org.qbrp.client.render.screen.context

import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.qbrp.client.render.dataformats.Vec2
import org.qbrp.client.render.item.model.data.ItemDisplay
import org.qbrp.client.render.screen.Alpha
import org.qbrp.client.render.screen.DefaultZIndexes
import org.qbrp.client.render.text.render.model.TextRenderer
import org.qbrp.main.engine.text.components.TextComponent

interface Draw2dContext {
    val tickDelta: Float
    val matrices: MatrixStack
    val vertexConsumers: VertexConsumerProvider

    fun beginAlpha(clear: Boolean = true)

    fun endAlpha()

    fun drawAlpha(alpha: Float)

    fun disposeAlpha()

    fun enableScissor(x1: Float, y1: Float, x2: Float, y2: Float)

    fun disableScissor()

    fun drawItem(display: ItemDisplay, x: Float, y: Float, scale: Float = 16f, z: Float = DefaultZIndexes.ITEM.value)

    fun drawBorder(x: Float, y: Float, width: Float, height: Float, z: Float, color: Int)

    fun drawTexturedQuad(
        texture: Identifier?, x1: Float, x2: Float, y1: Float, y2: Float, z: Float, u1: Float,
        u2: Float,
        v1: Float,
        v2: Float
    )

    fun drawTexture(texture: Identifier?, x1: Float, x2: Float, y1: Float, y2: Float, z: Float,
                    regionWidth: Float, regionHeight: Float, u: Float, v: Float, textureWidth: Float, textureHeight: Float
    ) {
        this.drawTexturedQuad(texture, x1, x2, y1, y2, z,
            (u + 0.0f) / textureWidth.toFloat(),
            (u + regionWidth.toFloat()) / textureWidth.toFloat(),
            (v + 0.0f) / textureHeight.toFloat(),
            (v + regionHeight.toFloat()) / textureHeight.toFloat()
        )
    }

    /**
     * Draws a textured rectangle from a region in a texture.
     */
    fun drawTexture(texture: Identifier?, x: Float, y: Float, width: Float, height: Float, u: Float, v: Float,
                    regionWidth: Float, regionHeight: Float, textureWidth: Float, textureHeight: Float
    ) {
        this.drawTexture(texture, x, x + width, y, y + height, 0f, regionWidth, regionHeight, u, v, textureWidth, textureHeight)
    }

    /**
     * Draws a textured rectangle from a region in a texture.
     *
     *
     * The width and height of the region are the same as
     * the dimensions of the rectangle.
     */
    fun drawTexture(
        texture: Identifier?, x: Float, y: Float, z: Float, u: Float, v: Float, width: Float, height: Float, textureWidth: Float, textureHeight: Float
    ) {
        this.drawTexture(texture, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight)
    }

    fun drawTexture(texture: Identifier?, x: Float, y: Float, u: Int, v: Int, width: Float, height: Float) {
        this.drawTexture(texture, x, y, 0f, u.toFloat(), v.toFloat(), width, height, 256f, 256f)
    }

    /**
     * Draws a textured rectangle from a region in a texture.
     *
     *
     * The width and height of the region are the same as
     * the dimensions of the rectangle.
     */
    fun drawTexture(texture: Identifier?, x: Float, y: Float, u: Float, v: Float, width: Float, height: Float, textureWidth: Float, textureHeight: Float) {
        this.drawTexture(texture, x, y, width, height, u, v, width, height, textureWidth, textureHeight)
    }

    fun fillWhiteTexture(x1: Float, y1: Float, x2: Float, y2: Float, z: Float = DefaultZIndexes.BACKGROUND.value)

    fun fillGradientVertical(x1: Float, y1: Float, x2: Float, y2: Float, z: Float, startColor: Int, endColor: Int)

    fun fillGradientHorizontal(x1: Float, y1: Float, x2: Float, y2: Float, z: Float, startColor: Int, endColor: Int)

    fun fill(x1: Float, y1: Float, x2: Float, y2: Float, z: Float, color: Int)

    fun fill(x1: Float, y1: Float, x2: Float, y2: Float, color: Int) {
        fill(x1, y1, x2, y2, DefaultZIndexes.BACKGROUND.value, color)
    }

    fun drawText(textRenderer: TextRenderer, text: TextComponent, pos: Vec2, z: Float = DefaultZIndexes.TEXT.value): Int

    fun draw()
}