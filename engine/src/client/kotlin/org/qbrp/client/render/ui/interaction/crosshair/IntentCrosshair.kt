package org.qbrp.client.render.ui.interaction.crosshair

import org.qbrp.client.engine.interactions.service.ClientInteractionManager
import org.qbrp.client.render.screen.context.Draw2dContext
import org.qbrp.client.render.ui.components.effect.Blend
import org.qbrp.client.render.ui.components.transform.old.HorizontalCentralisation
import org.qbrp.client.render.ui.components.transform.old.VerticalCentralisation
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.controls.entity.interactions.Intent

class IntentCrosshair(private val manager: ClientInteractionManager) {
    val cross: Cross = Cross(Colors.CROSSHAIR, 0.8f, 6f).apply {
        add(VerticalCentralisation(componentContext))
        add(HorizontalCentralisation(componentContext))
        add(Blend(componentContext))
    }

    fun render() {
        cross.apply {
            when (manager.intent) {
                Intent.DEFAULT -> {
                    horizontalPart.width = 0f
                    verticalPart.height = 0.8f
                    rotate = 45f
                }
                Intent.FINE -> {
                    horizontalPart.width = 0.8f
                    horizontalPart.height = 6f
                    verticalPart.height = 0.8f
                    verticalPart.height = 6f
                    rotate = 0f
                }
                Intent.ROUGH -> {
                    horizontalPart.height = 0.6f
                    horizontalPart.width = 0.8f
                    verticalPart.height = 6f
                    verticalPart.width = 0.8f
                    rotate = 45f
                }
            }
        }
    }
}