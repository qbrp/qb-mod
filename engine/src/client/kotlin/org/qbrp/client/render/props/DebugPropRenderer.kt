package org.qbrp.client.render.props

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import org.joml.Math.lerp
import org.joml.Matrix4f
import org.joml.Vector3f
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.client.engine.props.PropData
import org.qbrp.client.render.debug.DebugRenderContext
import org.qbrp.main.engine.physics.dynamics.dataformats.engine
import org.qbrp.main.engine.physics.dynamics.dataformats.joml
import org.qbrp.main.engine.physics.dynamics.shapes.BoxShape

/**
 * Рендерер пропа для отладки, показывает хитбокс вместо предмета
 */
class DebugPropRenderer() {
    fun render(debugCtx: DebugRenderContext, prop: PropData, shape: BoxShape, ctx: WorldRenderContext) {
        prop.updateFactor(ctx.tickDelta() / 20.0)
        val pos = prop.transform.pos
        val rot = prop.transform.quat

        debugCtx.renderDot(pos.minecraft(), 0.1f)

        val cameraPos = ctx.camera().pos.engine()
        val worldPos = pos.sub(cameraPos)

        val stack = ctx.matrixStack()
        stack.push()
        stack.translate(worldPos.x, worldPos.y, worldPos.z)
        stack.multiply(rot.joml())

        stack.translate(-shape.xHalf, -shape.yHalf, -shape.zHalf)

        val entry = stack.peek()
        val matrix = entry.positionMatrix
        val vertexConsumer: VertexConsumer = ctx.consumers()!!.getBuffer(RenderLayer.getLines())
        renderBoxLines(matrix, vertexConsumer, shape.sizeVector().joml(), 0f, 1f, 0f, 1f)

        stack.pop()
    }

    private fun renderBoxLines(
        matrix: Matrix4f,
        vertexConsumer: VertexConsumer,
        half: Vector3f,
        r: Float, g: Float, b: Float, a: Float
    ) {
        val corners = arrayOf(
            Vector3f(-half.x, -half.y, -half.z), Vector3f( half.x, -half.y, -half.z),
            Vector3f( half.x,  half.y, -half.z), Vector3f(-half.x,  half.y, -half.z),
            Vector3f(-half.x, -half.y,  half.z), Vector3f( half.x, -half.y,  half.z),
            Vector3f( half.x,  half.y,  half.z), Vector3f(-half.x,  half.y,  half.z)
        )
        val edges = arrayOf(
            0 to 1, 1 to 2, 2 to 3, 3 to 0,
            4 to 5, 5 to 6, 6 to 7, 7 to 4,
            0 to 4, 1 to 5, 2 to 6, 3 to 7
        )
        for ((i0, i1) in edges) {
            val c0 = corners[i0]
            val c1 = corners[i1]
            vertexConsumer
                .vertex(matrix, c0.x, c0.y, c0.z)
                .color(0, 255, 0, 240)
                .simple()
            vertexConsumer
                .vertex(matrix, c1.x, c1.y, c1.z)
                .color(0, 255, 0, 240)
                .simple()
        }
    }
}
