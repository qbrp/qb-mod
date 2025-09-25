package org.qbrp.client.render.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.SimpleFramebuffer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.*
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11

object Alpha {
    private val mc: MinecraftClient = MinecraftClient.getInstance()
    private var fbo: SimpleFramebuffer? = null
    private var fbW = 0
    private var fbH = 0

    private fun ensureFbo() {
        val win = mc.window
        val w = win.framebufferWidth
        val h = win.framebufferHeight
        if (fbo == null || w != fbW || h != fbH) {
            fbo?.delete()
            fbo = SimpleFramebuffer(w, h, true, MinecraftClient.IS_SYSTEM_MAC)
            fbW = w
            fbH = h
        }
    }

    fun begin(clear: Boolean = true) {
        ensureFbo()
        fbo!!.beginWrite(true)
        RenderSystem.viewport(0, 0, fbW, fbH)
        if (clear) {
            RenderSystem.clearColor(0f, 0f, 0f, 0f)
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT, false)
        }
    }

    fun end() {
        mc.framebuffer.beginWrite(true)
        val win = mc.window
        RenderSystem.viewport(0, 0, win.framebufferWidth, win.framebufferHeight)
    }

    fun draw(context: DrawContext, alpha: Float) {
        val fb = fbo ?: return

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha.coerceIn(0f, 1f))
        RenderSystem.setShaderTexture(0, fb.colorAttachment)

        val matrices = context.matrices
        val win = mc.window
        val sw = win.scaledWidth.toFloat()
        val sh = win.scaledHeight.toFloat()

        texturedQuad(matrices.peek().positionMatrix, 0f, 0f, sw, sh)

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.disableBlend()
    }

    fun dispose() {
        fbo?.delete()
        fbo = null
    }

    private fun texturedQuad(matrix: Matrix4f, x: Float, y: Float, w: Float, h: Float) {
        val tesselator = Tessellator.getInstance()
        val buf = tesselator.buffer
        buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)

        buf.vertex(matrix, x, y + h, 0f).texture(0f, 1f).next()
        buf.vertex(matrix, x + w, y + h, 0f).texture(1f, 1f).next()
        buf.vertex(matrix, x + w, y, 0f).texture(1f, 0f).next()
        buf.vertex(matrix, x, y, 0f).texture(0f, 0f).next()

        BufferRenderer.drawWithGlobalProgram(buf.end())
    }
}