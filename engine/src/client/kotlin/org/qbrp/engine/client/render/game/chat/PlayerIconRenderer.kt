package org.qbrp.engine.client.render.game.chat

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.qbrp.engine.chat.ChatModule
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.engine.chat.ChatModuleClient
import org.qbrp.system.utils.format.Format.asIdentifier
import kotlin.math.PI
import kotlin.math.sin

class PlayerIconRenderer {
    private val CIRCLE_TEXTURE = "textures/gui/white_circle.png".asIdentifier()
    private val animationSpeed = 0.2f
    private val minBrightness = 4f
    private val maxBrightness = 7f

    private fun calculateDynamicBrightness(entity: Entity): Pair<Float, Float> {
        val lightLevel = entity.world.getBrightness(entity.blockPos).coerceIn(0f, 1f)
        val darknessFactor = 1.2f - lightLevel
        return Pair(
            (minBrightness * darknessFactor).coerceIn(0.1f, 0.5f),
            (maxBrightness * darknessFactor).coerceIn(0.4f, 0.9f)
        )
    }

    fun initialize() {
        WorldRenderEvents.AFTER_ENTITIES.register(WorldRenderEvents.AfterEntities { context ->
            val client = MinecraftClient.getInstance()
            val world = client.world ?: return@AfterEntities
            val chatAPI: ChatModuleClient.API? = EngineClient.getChatModuleAPI()

            world.players.forEach { entity ->
                if (client.player?.canSee(entity) == true && entity != client.player && chatAPI?.isPlayerWriting(entity) == true) {
                    val totalPoints = 3
                    val baseY = entity.pos.y + entity.height + 0.35

                    val positions = listOf(
                        Vec3d(-0.22, 0.0, 0.0),
                        Vec3d(0.0, 0.0, 0.0),
                        Vec3d(0.22, 0.0, 0.0)
                    )

                    val matrices = context.matrixStack()
                    val camera = context.camera()

                    matrices.push()
                    matrices.translate(
                        entity.x - camera.pos.x,
                        baseY - camera.pos.y,
                        entity.z - camera.pos.z
                    )

                    matrices.multiply(camera.rotation)
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f))

                    matrices.push()
                    matrices.scale(0.67f, 0.19f, 1f) // Ширина 0.4, высота 0.1
                    drawBgQuad(matrices.peek().positionMatrix, 0f)
                    matrices.pop()

                    positions.forEachIndexed { index, pos ->
                        matrices.push()
                        matrices.translate(pos.x, pos.y, pos.z)
                        drawBillboardQuad(context, matrices, 0.09f, index, totalPoints, entity)
                        matrices.pop()
                    }

                    matrices.pop()
                }
            }
        })
    }

    private var animationTime = 0f

    private fun drawBillboardQuad(
        context: WorldRenderContext,
        matrices: MatrixStack,
        size: Float,
        index: Int,
        totalPoints: Int,
        entity: Entity
    ) {
        animationTime += context.tickDelta() * animationSpeed

        matrices.scale(size, size, 1f)

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
        RenderSystem.setShaderTexture(0, CIRCLE_TEXTURE)
        RenderSystem.setShader { GameRenderer.getPositionTexColorProgram() }

        val phaseOffset = (index.toFloat() / totalPoints) * PI.toFloat() * 1.5f
        val progress = animationTime * 0.5f + phaseOffset
        val brightness = (sin(progress) + 1) / 2
        val smoothBrightness = brightness * brightness

        val (currentMin, currentMax) = calculateDynamicBrightness(entity)
        val colorValue = lerp(currentMin, currentMax, smoothBrightness)

        val buffer = Tessellator.getInstance().buffer
        val matrix = matrices.peek().positionMatrix

        buffer.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE_COLOR)
        buildQuad(buffer, matrix, colorValue)
        BufferRenderer.drawWithGlobalProgram(buffer.end())

        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t

    private fun buildQuad(buffer: BufferBuilder, matrix: Matrix4f, brightness: Float) {
        val alpha = 0.7f

        buffer.vertex(matrix, -0.5f, -0.5f, 0f)
            .texture(0f, 1f)
            .color(brightness, brightness, brightness, alpha)
            .next()
        buffer.vertex(matrix, 0.5f, -0.5f, 0f)
            .texture(1f, 1f)
            .color(brightness, brightness, brightness, alpha)
            .next()
        buffer.vertex(matrix, 0.5f, 0.5f, 0f)
            .texture(1f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()

        buffer.vertex(matrix, 0.5f, 0.5f, 0f)
            .texture(1f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()
        buffer.vertex(matrix, -0.5f, 0.5f, 0f)
            .texture(0f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()
        buffer.vertex(matrix, -0.5f, -0.5f, 0f)
            .texture(0f, 1f)
            .color(brightness, brightness, brightness, alpha)
            .next()
    }

    private fun drawBgQuad(matrix: Matrix4f, brightness: Float) {
        val tessellator = Tessellator.getInstance()
        val buffer = tessellator.buffer
        val alpha = 0.2f

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.disableDepthTest()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }

        buffer.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR)

        buffer.vertex(matrix, -0.5f, -0.5f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()
        buffer.vertex(matrix, 0.5f, -0.5f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()
        buffer.vertex(matrix, 0.5f, 0.5f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()

        buffer.vertex(matrix, 0.5f, 0.5f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()
        buffer.vertex(matrix, -0.5f, 0.5f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()
        buffer.vertex(matrix, -0.5f, -0.5f, 0f)
            .color(brightness, brightness, brightness, alpha)
            .next()

        BufferRenderer.drawWithGlobalProgram(buffer.end())

        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()
    }
}