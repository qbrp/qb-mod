package org.qbrp.client.render.ui.interaction.mode

import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.transform.Pivot
import org.qbrp.client.render.ui.dsl.viewOf
import org.qbrp.main.engine.Colors
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor
import org.qbrp.main.engine.text.toSequenceBuilder

context(ui: UiContext)
fun noClipSwitcher(stateSupplier: () -> Boolean) = viewOf {
    background(Colors.BACKGROUND, -2f)
    anchors(1f, 1f, Pivot(1f, 1f))
    margin(0f, 0f, 0f, 4f)
    overlayBehaviour()
    text {
        literal("NOCLIP : ")
            .toSequenceBuilder()
            .add(
                when (stateSupplier()) {
                    true -> literal("Включен", TextColor.GREEN)
                    false -> literal("Выключен", TextColor.RED)
                }
            )
            .build()
    }
}

context(ui: UiContext)
fun interactionModeSwitcher(modeSupplier: () -> InteractionMode) = viewOf {
    overlayBehaviour()
    background(Colors.BACKGROUND, -2f)
    anchors(1f, 1f, Pivot(1f, 1f))
    margin(0f, 0f, 0f, 4f)
    text {
        val mode = modeSupplier()
        literal(mode.displayName, Style.ofColor(TextColor(mode.color)))
    }
}

val InteractionMode.color
    get() = when (this) {
        InteractionMode.DEFAULT -> Colors.WHITE
        InteractionMode.BUILDER -> Colors.GREEN
        InteractionMode.CROW -> Colors.GRAY
        InteractionMode.GM -> Colors.RED
}