package org.qbrp.client.render.debug

import net.minecraft.client.render.Camera
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import org.qbrp.client.render.props.simple
import org.qbrp.main.engine.physics.dynamics.dataformats.engine

class DebugRenderContext(val matrixStack: MatrixStack, val vertexConsumer: VertexConsumerProvider, val camera: Camera) {
    fun renderDot(pos: Vec3d, size: Float) {
        val cameraPos = camera.pos
        val center = pos.subtract(cameraPos)
        val halfSize = size / 2

        matrixStack.push()
        matrixStack.translate(center.x.toFloat(), center.y.toFloat(), center.z.toFloat())
        matrixStack.multiply(camera.rotation)  // billboarding
        val entry = matrixStack.peek()
        val model = entry.positionMatrix
        val buf = vertexConsumer.getBuffer(RenderLayer.getLines())

        buf.vertex(model, -halfSize, 0f, 0f)
            .color(255, 0, 0, 255)
            .simple()
        buf.vertex(model,  halfSize, 0f, 0f)
            .color(255, 0, 0, 255)
            .simple()

        buf.vertex(model, 0f, -halfSize, 0f)
            .color(255, 0, 0, 255)
            .simple()
        buf.vertex(model, 0f,  halfSize, 0f)
            .color(255, 0, 0, 255)
            .simple()

        matrixStack.pop()
    }
}