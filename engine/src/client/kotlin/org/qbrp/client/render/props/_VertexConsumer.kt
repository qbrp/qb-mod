package org.qbrp.client.render.props

import net.minecraft.client.render.VertexConsumer

fun VertexConsumer.simple() {
    texture(0f, 0f)
    overlay(0, 0)
    light(0, 240)
    normal(0f, 1f, 0f)
    next()
}