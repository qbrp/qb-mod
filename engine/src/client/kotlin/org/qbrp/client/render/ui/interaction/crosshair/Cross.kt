package org.qbrp.client.render.ui.interaction.crosshair

import org.qbrp.client.render.ui.view.View
import org.qbrp.client.render.ui.components.texture.Background
import org.qbrp.client.render.ui.components.transform.old.HorizontalCentralisation
import org.qbrp.client.render.ui.components.transform.old.VerticalCentralisation
import org.qbrp.main.core.components.extensions.add

class Cross(val color: Int, val thick: Float, val length: Float): View() {
    val verticalPart = View().apply {
        width = thick
        height = length
        add(VerticalCentralisation(componentContext))
        add(HorizontalCentralisation(componentContext))
        add(Background(this.componentContext, color, texture = true))
    }
    val horizontalPart = View().apply {
        height = thick
        width = length
        add(VerticalCentralisation(componentContext))
        add(HorizontalCentralisation(componentContext))
        add(Background(this.componentContext, color, texture = true))
    }

    init {
        addView(verticalPart)
        addView(horizontalPart)
    }
}